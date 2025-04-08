import requests
import json
from typing import Dict
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

    async def extract_text(self, image_data: str, file_format: str = 'jpg') -> Dict:
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

            return self._parse_receipt_data(result)

        except ReceiptException:
            raise
        except Exception as e:
            logger.error(f"OCR 처리 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.OCR_ERROR)

    def _parse_receipt_data(self, ocr_result: Dict) -> Dict:
        """OCR 결과 파싱"""
        try:
            receipt_data = ocr_result['images'][0]['receipt']['result']

            # 상호명 추출
            store_name = receipt_data.get('storeInfo', {}).get('name', {}).get('formatted', {}).get('value', '알 수 없음')
            logger.info(f"추출된 상호명: {store_name}")

            # 상품 정보 추출
            items = []
            item_id = 1
            for subresult in receipt_data.get('subResults', []):
                for item in subresult.get('items', []):
                    item_data = {
                        'itemId': item_id,
                        'itemName': item['name']['formatted']['value'],
                        'itemQuantity': int(item['count']['formatted']['value']),
                        'itemAmount': int(item['price']['unitPrice']['formatted']['value'])
                    }
                    items.append(item_data)
                    item_id += 1
                    logger.info(f"추출된 상품 정보: {item_data}")

            # 총액 추출 - 각 상품의 단가 * 수량의 합계로 계산
            total_amount = sum(item['itemAmount'] * item['itemQuantity'] for item in items)
            logger.info(f"추출된 총액: {total_amount}")

            # 결과 데이터 구성
            parsed_data = {
                'storeName': store_name,
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