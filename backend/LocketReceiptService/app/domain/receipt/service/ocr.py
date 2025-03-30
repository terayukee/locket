import requests
import json
from typing import Dict
from app.config.settings import settings
from app.common.constant.status import ErrorMessage
from ..exception.exception import OCRProcessingException

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
            receipt_data = ocr_result['images'][0]['receipt']['result']

            # 상점명 추출
            store_name = receipt_data.get('storeInfo', {}).get('name', {}).get('formatted', {}).get('value', '알 수 없음')
            print(f"추출된 상호명: {store_name}")

            # 상품 목록 추출
            items = []
            item_id = 1  # 아이템 ID 초기화
            for subresult in receipt_data.get('subResults', []):
                for item in subresult.get('items', []):
                    item_data = {
                        'itemId': item_id,
                        'itemName': item['name']['formatted']['value'],
                        'itemQuantity': int(item['count']['formatted']['value']),
                        'itemAmount': int(item['price']['price']['formatted']['value'])
                    }
                    items.append(item_data)
                    item_id += 1
                    print(f"추출된 상품 정보:", item_data)

            # 총액 추출
            total_amount = int(receipt_data['totalPrice']['price']['formatted']['value'])
            print(f"추출된 총액: {total_amount}")

            parsed_data = {
                'storeName': store_name,
                'items': items,
                'totalAmount': total_amount
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