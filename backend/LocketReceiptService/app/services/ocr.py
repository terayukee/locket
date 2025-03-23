import requests
import json
import base64
from typing import Dict
from decimal import Decimal
from ..config import settings

class OCRService:
    def __init__(self):
        self.api_url = settings.CLOVA_OCR_URL
        self.secret = settings.CLOVA_OCR_SECRET
        self.headers = {
            'X-OCR-SECRET': self.secret,
            'Content-Type': 'application/json'
        }

    async def extract_text(self, image_data: str) -> Dict:
        request_json = {
            'images': [
                {
                    'format': 'jpg',
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
            print("Sending request to OCR API...")
            response = requests.post(self.api_url, headers=self.headers, json=request_json)
            print(f"Response status: {response.status_code}")
            result = response.json()
            print(f"Response body: {json.dumps(result, indent=2)}")

            if response.status_code != 200:
                raise Exception(f"OCR API Error: {result.get('error', {}).get('message')}")

            return self._parse_receipt_data(result)

        except Exception as e:
            print(f"Error occurred: {str(e)}")
            raise Exception(f"OCR 처리 중 오류 발생: {str(e)}")

    def _parse_receipt_data(self, ocr_result: Dict) -> Dict:
        """OCR 결과에서 영수증 데이터 파싱"""
        try:
            receipt_data = ocr_result['images'][0]['receipt']['result']

            # 상점 정보 추출
            store_info = receipt_data.get('storeInfo', {})
            store_name = store_info.get('name', {}).get('formatted', {}).get('value', '')
            if store_info.get('subName', {}).get('text'):
                store_name += ' ' + store_info['subName']['text']
            business_number = store_info.get('bizNum', {}).get('formatted', {}).get('value', '')

            # 결제 날짜 추출
            payment_date = receipt_data.get('paymentInfo', {}).get('date', {}).get('formatted', {})
            date_str = f"{payment_date.get('year', '')}/{payment_date.get('month', '')}/{payment_date.get('day', '')}"

            # 상품 목록 추출
            items = []
            for subresult in receipt_data.get('subResults', []):
                for item in subresult.get('items', []):
                    items.append({
                        'name': item['name']['formatted']['value'],
                        'quantity': int(item['count']['formatted']['value']),
                        'price': str(item['price']['price']['formatted']['value'])
                    })

            # 총액 추출
            total_amount = str(receipt_data['totalPrice']['price']['formatted']['value'])

            return {
                'store_name': store_name,
                'business_number': business_number,
                'payment_date': date_str,
                'total_amount': total_amount,
                'items': items
            }

        except Exception as e:
            print(f"Parsing error: {str(e)}")
            raise Exception(f"영수증 데이터 파싱 중 오류 발생: {str(e)}")