from typing import List, Dict
from openai import AsyncOpenAI
from app.config.settings import settings
from ..exception.receipt_exception import ReceiptException
from ..constant.receipt_error import ReceiptErrorCode
import logging

logger = logging.getLogger(__name__)

class ItemClassifier:
    """상품 분류 서비스"""
    CATEGORIES = ["식비", "카페/디저트", "쇼핑", "생활", "교통", "기타"]

    def __init__(self):
        try:
            self.client = AsyncOpenAI(
                api_key=settings.OPENAI_API_KEY,
                base_url="https://api.openai.com/v1"
            )
        except Exception as e:
            logger.error(f"OpenAI 클라이언트 초기화 실패: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

    async def classify_receipt(self, receipt_data: Dict) -> Dict:
        """영수증 품목 분류"""
        try:
            logger.info("품목 분류 시작")
            classified_items = await self._classify_items(receipt_data["items"])

            result = {
                'items': classified_items,
                'totalAmount': receipt_data['totalAmount']
            }

            return result

        except Exception as e:
            logger.error(f"품목 분류 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

    async def _classify_items(self, items: List[Dict]) -> List[Dict]:
        """개별 품목 분류"""
        try:
            prompt = self._create_prompt([item['itemName'] for item in items])
            logger.info(f"GPT 요청 프롬프트: {prompt}")

            response = await self.client.chat.completions.create(
                model="gpt-4o",
                messages=[
                    {"role": "system", "content": "상품명을 기반으로 카테고리를 분류하는 전문가입니다."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=100
            )

            categories = self._parse_response(response.choices[0].message.content)
            logger.info(f"분류 결과: {categories}")

            if len(categories) != len(items):
                logger.error(f"품목 수({len(items)})와 카테고리 수({len(categories)})가 일치하지 않습니다")
                raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

            classified_items = []
            for item, category in zip(items, categories):
                if category.strip() not in self.CATEGORIES:
                    logger.error(f"유효하지 않은 카테고리입니다: {category}")
                    raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

                classified_item = {
                    'itemId': item['itemId'],
                    'itemName': item['itemName'],
                    'itemQuantity': item['itemQuantity'],
                    'itemAmount': item['itemAmount'],
                    'itemCategory': category.strip()
                }
                classified_items.append(classified_item)

            return classified_items

        except ReceiptException:
            raise
        except Exception as e:
            logger.error(f"품목 분류 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

    def _create_prompt(self, item_names: List[str]) -> str:
        """프롬프트 생성"""
        categories = ", ".join(self.CATEGORIES)
        return f"""
        다음 상품들을 카테고리로 분류해주세요:
        상품: {item_names}
        카테고리: {categories}
        
        각 상품에 대해 가장 적절한 카테고리 하나만 선택해주세요.
        응답 형식: 카테고리1|카테고리2|카테고리3
        예시 응답: 식비|카페/디저트|카페/디저트
        
        주의사항:
        - 정확히 위 형식으로만 응답해주세요
        - 카테고리 사이에 공백을 넣지 마세요
        - 추가 설명이나 다른 텍스트를 포함하지 마세요
        """

    def _parse_response(self, response: str) -> List[str]:
        """LLM 응답을 파싱하여 카테고리 리스트 반환"""
        response = response.strip()

        # 단일 카테고리인 경우 처리
        if '|' not in response:
            if response in self.CATEGORIES:  # 유효한 카테고리인지 확인
                return [response]
            logger.error(f"잘못된 응답 형식 또는 카테고리입니다: {response}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

        # 여러 카테고리인 경우 처리
        categories = [category.strip() for category in response.split('|')]

        # 모든 카테고리가 유효한지 확인
        if not all(category in self.CATEGORIES for category in categories):
            logger.error(f"잘못된 카테고리가 포함되어 있습니다: {categories}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

        return categories