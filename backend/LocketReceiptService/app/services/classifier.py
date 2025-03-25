from typing import List, Dict
import openai
import json
from ..config import settings
from ..exceptions.receipt_exceptions import ClassificationException
from ..constants.status import ErrorMessage

class ItemClassifier:
    CATEGORIES = ["식비", "카페/디저트", "쇼핑", "생활", "교통", "기타"]

    def __init__(self):
        self.client = openai.AsyncOpenAI(api_key=settings.OPENAI_API_KEY)

    async def classify_receipt(self, receipt_data: Dict) -> Dict:
        try:
            print("품목 분류 시작")
            # 품목 분류
            classified_items = await self._classify_items(receipt_data["items"])

            # 할인금액 계산
            total_sum = 0
            biggest_price = 0
            biggest_price_index = 0

            # 가장 큰 price 찾기
            for i, item in enumerate(classified_items):
                price = float(item['price'])
                total_sum += price
                if price > biggest_price:
                    biggest_price = price
                    biggest_price_index = i

            # 할인금액 계산
            actual_total = float(receipt_data["total_amount"])
            discount = total_sum - actual_total

            # 할인금액이 있으면 가장 큰 price를 가진 품목에서 차감
            if discount > 0:
                biggest_price_item = classified_items[biggest_price_index]
                biggest_price_item['price'] = str(int(float(biggest_price_item['price']) - discount))

            receipt_data["items"] = classified_items
            receipt_data["category_totals"] = self._calculate_category_totals(classified_items)

            return receipt_data

        except Exception as e:
            error_msg = f"품목 분류 중 오류 발생: {str(e)}"
            print(error_msg)
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=error_msg
            )

    async def _classify_items(self, items: List[Dict]) -> List[Dict]:
        """개별 품목 분류"""
        try:
            # 프롬프트 구성
            prompt = self._create_prompt(items)
            print(f"GPT 요청 프롬프트: {prompt}")

            # GPT 호출
            response = await self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "다음 상품들을 주어진 카테고리로 분류해주세요. 정확히 주어진 형식으로만 응답해주세요."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3  # 더 일관된 응답을 위해 temperature 낮춤
            )

            response_content = response.choices[0].message.content
            print(f"GPT 응답: {response_content}")

            # 응답 파싱 및 카테고리 할당
            categories = self._parse_response(response_content)
            print(f"파싱된 카테고리: {categories}")

            # 카테고리 수 확인
            if len(categories) != len(items):
                raise ValueError(f"카테고리 수({len(categories)})가 상품 수({len(items)})와 일치하지 않습니다.")

            # 아이템에 카테고리 추가
            classified_items = []
            for item, category in zip(items, categories):
                if category.strip() not in self.CATEGORIES:
                    raise ValueError(f"유효하지 않은 카테고리입니다: {category}")
                item_with_category = item.copy()
                item_with_category['category'] = category.strip()
                classified_items.append(item_with_category)

            return classified_items

        except Exception as e:
            error_msg = f"품목 분류 중 오류 발생: {str(e)}"
            print(error_msg)
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=error_msg
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
        예시 응답: 식비|카페/디저트|카페/디저트
        
        주의사항:
        - 정확히 위 형식으로만 응답해주세요
        - 카테고리 사이에 공백을 넣지 마세요
        - 추가 설명이나 다른 텍스트를 포함하지 마세요
        """

    def _parse_response(self, response: str) -> List[str]:
        """LLM 응답을 파싱하여 카테고리 리스트 반환"""
        response = response.strip()
        if '|' not in response:
            raise ValueError(f"잘못된 응답 형식입니다: {response}")

        return [category.strip() for category in response.split('|')]

    def _calculate_category_totals(self, items: List[Dict]) -> Dict[str, float]:
        """카테고리별 합계 계산"""
        totals = {}
        for item in items:
            category = item['category']
            amount = float(item['price'])  # quantity 곱하지 않음
            totals[category] = totals.get(category, 0) + amount
        return totals