import requests
import json
import base64
from typing import Dict
from decimal import Decimal
from ..config import settings
from ..constants.status import ErrorMessage
from ..exceptions.receipt_exceptions import OCRProcessingException

class OCRService:
    def __init__(self):
        self.api_url = settings.CLOVA_OCR_URL
        self.secret = settings.CLOVA_OCR_SECRET
        self.headers = {
            'X-OCR-SECRET': self.secret,
            'Content-Type': 'application/json'
        }

    async def extract_text(self, image_data: str, file_format: str = 'jpg') -> Dict:
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
            print("OCR 요청 URL:", self.api_url)
            print("OCR 요청 헤더:", {k: '***' if k == 'X-OCR-SECRET' else v for k, v in self.headers.items()})
            print("OCR 요청 데이터:", {
                **request_json,
                'images': [{**img, 'data': '...'} for img in request_json['images']]
            })

            response = requests.post(self.api_url, headers=self.headers, json=request_json)
            print(f"OCR 응답 상태 코드: {response.status_code}")

            result = response.json()
            print(f"OCR 응답 데이터: {json.dumps(result, indent=2, ensure_ascii=False)}")

            if response.status_code != 200:
                error_detail = f"OCR API Error: {result.get('error', {}).get('message', '알 수 없는 오류')}"
                print(f"OCR 오류: {error_detail}")
                raise OCRProcessingException(
                    message=ErrorMessage.OCR_PROCESSING_ERROR,
                    detail=error_detail
                )

            return self._parse_receipt_data(result)

        except OCRProcessingException:
            raise
        except Exception as e:
            error_msg = f"OCR 처리 중 예상치 못한 오류 발생: {str(e)}"
            print(error_msg)
            raise OCRProcessingException(
                message=ErrorMessage.OCR_PROCESSING_ERROR,
                detail=error_msg
            )

    def _parse_receipt_data(self, ocr_result: Dict) -> Dict:
        """OCR 결과에서 영수증 데이터 파싱"""
        try:
            print("OCR 결과 파싱 시작")
            print("전체 OCR 결과:", json.dumps(ocr_result, indent=2, ensure_ascii=False))

            receipt_data = ocr_result['images'][0]['receipt']['result']
            print("영수증 데이터:", json.dumps(receipt_data, indent=2, ensure_ascii=False))

            # 상점 정보 추출
            store_info = receipt_data.get('storeInfo', {})
            store_name = store_info.get('name', {}).get('formatted', {}).get('value', '')
            if store_info.get('subName', {}).get('text'):
                store_name += ' ' + store_info['subName']['text']
            business_number = store_info.get('bizNum', {}).get('formatted', {}).get('value', '')
            print(f"추출된 상점 정보 - 상점명: {store_name}, 사업자번호: {business_number}")

            # 결제 날짜 추출
            payment_info = receipt_data.get('paymentInfo', {})
            if payment_info:
                date_info = payment_info.get('date', {}).get('text', '')
                if not date_info:
                    for field in receipt_data.get('subResults', []):
                        if 'date' in field:
                            date_info = field['date'].get('text', '')
                            break
                payment_date = date_info
            else:
                payment_date = "날짜 정보 없음"
            print(f"추출된 결제 날짜: {payment_date}")

            # 상품 목록 추출
            items = []
            for subresult in receipt_data.get('subResults', []):
                for item in subresult.get('items', []):
                    item_data = {
                        'name': item['name']['formatted']['value'],
                        'quantity': int(item['count']['formatted']['value']),
                        'price': str(item['price']['price']['formatted']['value'])
                    }
                    items.append(item_data)
                    print(f"추출된 상품 정보:", item_data)

            # 총액 추출
            total_amount = str(receipt_data['totalPrice']['price']['formatted']['value'])
            print(f"추출된 총액: {total_amount}")

            parsed_data = {
                'store_name': store_name,
                'business_number': business_number,
                'payment_date': payment_date,
                'total_amount': total_amount,
                'items': items
            }
            print("최종 파싱 결과:", json.dumps(parsed_data, indent=2, ensure_ascii=False))

            return parsed_data

        except KeyError as e:
            error_msg = f"필수 필드를 찾을 수 없습니다: {str(e)}"
            print(f"파싱 오류 (KeyError): {error_msg}")
            raise OCRProcessingException(
                message=ErrorMessage.OCR_PARSING_ERROR,
                detail=error_msg
            )
        except Exception as e:
            error_msg = f"영수증 데이터 파싱 중 오류 발생: {str(e)}"
            print(f"파싱 오류: {error_msg}")
            raise OCRProcessingException(
                message=ErrorMessage.OCR_PARSING_ERROR,
                detail=error_msg
            )