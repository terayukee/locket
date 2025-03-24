from typing import List, Dict
import openai
from ..config import settings
from ..exceptions.receipt_exceptions import ClassificationException
from ..constants.status import ErrorMessage

class ItemClassifier:
    CATEGORIES = ["식비", "카페/디저트", "쇼핑", "생활", "교통", "기타"]

    def __init__(self):
        openai.api_key = settings.OPENAI_API_KEY

    async def classify_receipt(self, receipt_data: Dict) -> Dict:
        """영수증 전체 데이터에서 품목들을 분류"""
        try:
            # 품목 분류
            classified_items = await self._classify_items(receipt_data["items"])

            # 원본 영수증 데이터에 분류 결과 추가
            receipt_data["items"] = classified_items

            # 카테고리별 합계 계산
            category_totals = self._calculate_category_totals(classified_items)
            receipt_data["category_totals"] = category_totals

            return receipt_data

        except Exception as e:
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=str(e)
            )

    async def _classify_items(self, items: List[Dict]) -> List[Dict]:
        """개별 품목 분류"""
        try:
            # 프롬프트 구성
            prompt = self._create_prompt(items)

            # GPT 호출
            response = await openai.ChatCompletion.acreate(
                model="GPT-4o mini",
                messages=[
                    {"role": "system", "content": "다음 상품들을 주어진 카테고리로 분류해주세요."},
                    {"role": "user", "content": prompt}
                ]
            )

            # 응답 파싱 및 카테고리 할당
            categories = self._parse_response(response.choices[0].message.content)

            # 아이템에 카테고리 추가
            for item, category in zip(items, categories):
                item['category'] = category

            return items

        except Exception as e:
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=str(e)
            )

    def _create_prompt(self, items: List[Dict]) -> str:
        """프롬프트 생성"""
        item_names = [item['name'] for item in items]
        categories = ", ".join(self.CATEGORIES)

        return f"""
            다음 상품들을 카테고리로 분류해주세요:
            상품: {item_names}
            카테고리: {categories}
            
            각 상품에 대해 가장 적절한 카테고리 하나만 선택해주세요.
            응답 형식: 카테고리1|카테고리2|카테고리3
            """

    def _parse_response(self, response: str) -> List[str]:
        """LLM 응답을 파싱하여 카테고리 리스트 반환"""
        return response.strip().split('|')

    def _calculate_category_totals(self, items: List[Dict]) -> Dict[str, float]:
        """카테고리별 합계 계산"""
        totals = {}
        for item in items:
            category = item['category']
            amount = float(item['price']) * item['quantity']
            totals[category] = totals.get(category, 0) + amount
        return totals