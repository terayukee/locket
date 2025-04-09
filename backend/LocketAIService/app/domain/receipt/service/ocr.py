import requests
import json
from typing import Dict

import unicodedata

from app.config.settings import settings
from ..exception.receipt_exception import ReceiptException
from ..constant.receipt_error import ReceiptErrorCode
import logging

logger = logging.getLogger(__name__)

class OCRService:
    """영수증 OCR 서비스"""
    def __init__(self):
        self.api_url = settings.CLOVA_OCR_URL
        self.secret = settings.CLOVA_OCR_SECRET
        self.headers = {
            'X-OCR-SECRET': self.secret,
            'Content-Type': 'application/json'
        }


    def validate_store_name(self, ocr_store_name: str, payment_store_name: str) -> bool:
        """
        OCR로 인식한 상호명과 결제 정보의 상호명을 비교

        Args:
            ocr_store_name (str): OCR로 인식한 상호명
            payment_store_name (str): 결제 정보의 상호명

        Returns:
            bool: 상호명이 일치하면 True, 불일치하면 False
        """
        # 상호명 정규화 (공백 제거, 소문자 변환)
        ocr_name = ocr_store_name.replace(" ", "").lower()
        payment_name = payment_store_name.replace(" ", "").lower()

        # 둘 중 하나가 다른 하나에 포함되어 있으면 True
        return ocr_name in payment_name or payment_name in ocr_name


    async def extract_text(self, image_data: str, payment_store_name: str, file_format: str = 'jpg') -> Dict:
        """이미지에서 텍스트 추출"""
        request_json = {
            'images': [
                {
                    'format': file_format,
                    'name': 'receipt',
                    'data': image_data
                }
            ],
            'requestId': 'receipt_ocr_request',
            'version': 'V2',
            'timestamp': 0,
            'lang': 'ko'
        }

        try:
            # API 요청 로깅
            logger.info(f"OCR 요청 URL: {self.api_url}")
            logger.info(f"OCR 요청 헤더: {self._mask_secret(self.headers)}")
            logger.info(f"OCR 요청 데이터: {self._mask_image_data(request_json)}")

            # OCR API 호출
            response = requests.post(self.api_url, headers=self.headers, json=request_json)
            logger.info(f"OCR 응답 상태 코드: {response.status_code}")

            result = response.json()
            logger.info(f"OCR 응답 데이터: {json.dumps(result, indent=2, ensure_ascii=False)}")

            if response.status_code != 200:
                error_message = result.get('error', {}).get('message', '알 수 없는 오류')
                logger.error(f"OCR API Error: {error_message}")
                raise ReceiptException(error_code=ReceiptErrorCode.OCR_ERROR)

            return self._parse_receipt_data(result, payment_store_name)

        except ReceiptException:
            raise
        except Exception as e:
            logger.error(f"OCR 처리 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.OCR_ERROR)

    def _clean_text(self, text: str) -> str:
        """OCR 텍스트 후처리: 잘못 인식된 특수문자/숫자 보정"""
        if not text:
            return text
        text = unicodedata.normalize("NFC", text)  # 한글 정규화
        # 자주 발생하는 오타 패턴 교정
        corrections = {
            '화0트': '화이트',
            '0트': '이트',
            '보드마카세트(흑3개': '보드마카세트(흑3개입)',
            '"': '',  # 따옴표
            '.': '',  # 마침표
            '(': '', ')': '',  # 괄호
        }
        for wrong, correct in corrections.items():
            text = text.replace(wrong, correct)
        return text.strip()

    def _parse_receipt_data(self, ocr_result: Dict, payment_store_name: str) -> Dict:
        """OCR 결과 파싱"""
        try:
            receipt_data = ocr_result['images'][0]['receipt']['result']

            # 상호명 정리 적용
            store_name_raw = receipt_data.get('storeInfo', {}).get('name', {}).get('text', '알 수 없음')
            store_name = self._clean_text(store_name_raw)
            logger.info(f"추출된 상호명: {store_name}")

            # 상품 정보
            items = []
            item_id = 1
            for subresult in receipt_data.get('subResults', []):
                for item in subresult.get('items', []):
                    try:
                        quantity = int(item.get('count', {}).get('formatted', {}).get('value', 1))
                        if quantity <= 0:
                            raise ValueError
                    except Exception:
                        logger.warning(f"count 필드 누락 또는 비정상 값: 기본값 1 사용 - item: {item.get('name', {}).get('text', '이름 없음')}")
                        quantity = 1

                    try:
                        item_amount = int(item.get('price', {}).get('unitPrice', {}).get('formatted', {}).get('value'))
                        logger.info(f"영수증 형식 단가 추출: {item_amount}")
                    except (KeyError, TypeError, ValueError):
                        try:
                            total_price = int(item.get('price', {}).get('price', {}).get('formatted', {}).get('value'))
                            item_amount = total_price // quantity
                            logger.info(f"PDF 형식 단가 계산: {total_price} / {quantity} = {item_amount}")
                        except Exception as e:
                            logger.error(f"가격 정보를 찾을 수 없습니다: {str(e)}")
                            raise ReceiptException(error_code=ReceiptErrorCode.PARSING_ERROR)

                    # 상품명 정리 적용
                    raw_item_name = item.get('name', {}).get('text', '이름 없음')
                    item_name = self._clean_text(raw_item_name)

                    item_data = {
                        'itemId': item_id,
                        'itemName': item_name,
                        'itemQuantity': quantity,
                        'itemAmount': item_amount
                    }
                    items.append(item_data)
                    item_id += 1
                    logger.info(f"추출된 상품 정보: {item_data}")

            # 총액
            try:
                total_amount = int(receipt_data['totalPrice']['price']['formatted']['value'])
                logger.info(f"OCR 인식된 총액: {total_amount}")
            except KeyError:
                total_amount = sum(item['itemAmount'] * item['itemQuantity'] for item in items)
                logger.info(f"계산된 총액: {total_amount}")

            parsed_data = {
                'storeName': payment_store_name,
                'items': items,
                'totalAmount': total_amount
            }
            logger.info(f"최종 파싱 결과: {json.dumps(parsed_data, indent=2, ensure_ascii=False)}")
            return parsed_data

        except KeyError as e:
            logger.error(f"필수 필드를 찾을 수 없습니다: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.PARSING_ERROR)
        except Exception as e:
            logger.error(f"영수증 데이터 파싱 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.PARSING_ERROR)

    def _mask_secret(self, headers: Dict) -> Dict:
        """민감한 헤더 정보 마스킹"""
        return {k: '***' if k == 'X-OCR-SECRET' else v for k, v in headers.items()}

    def _mask_image_data(self, request_json: Dict) -> Dict:
        """이미지 데이터 마스킹"""
        return {
            **request_json,
            'images': [{**img, 'data': '...'} for img in request_json['images']]
        }