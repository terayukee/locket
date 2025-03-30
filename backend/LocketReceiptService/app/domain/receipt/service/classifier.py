from typing import List, Dict
from openai import AsyncOpenAI
from app.config.settings import settings
from app.common.constant.status import ErrorMessage
from ..exception.exception import ClassificationException

class ItemClassifier:
    CATEGORIES = ["식비", "카페/디저트", "쇼핑", "생활", "교통", "기타"]

    def __init__(self):
        try:
            # OpenAI 클라이언트 초기화
            self.client = AsyncOpenAI(
                api_key=settings.OPENAI_API_KEY,
                base_url="https://api.openai.com/v1"
            )
        except Exception as e:
            print(f"OpenAI 클라이언트 초기화 실패: {str(e)}")
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=f"OpenAI 클라이언트 초기화 실패: {str(e)}"
            )

    async def classify_receipt(self, receipt_data: Dict) -> Dict:
        try:
            print("품목 분류 시작")
            # 품목 분류
            classified_items = await self._classify_items(receipt_data["items"])

            # 결과 데이터 구성
            result = {
                'items': classified_items,
                'totalAmount': receipt_data['totalAmount']
            }

            return result

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
            prompt = self._create_prompt([item['itemName'] for item in items])
            print(f"GPT 요청 프롬프트: {prompt}")

            # GPT 호출
            response = await self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "다음 상품들을 주어진 카테고리로 분류해주세요. 정확히 주어진 형식으로만 응답해주세요."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3
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

                classified_item = {
                    'itemId': item['itemId'],
                    'itemName': item['itemName'],
                    'itemQuantity': item['itemQuantity'],
                    'itemAmount': item['itemAmount'],
                    'itemCategory': category.strip()
                }
                classified_items.append(classified_item)

            return classified_items

        except Exception as e:
            error_msg = f"품목 분류 중 오류 발생: {str(e)}"
            print(error_msg)
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=error_msg
            )

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
        if '|' not in response:
            raise ValueError(f"잘못된 응답 형식입니다: {response}")

        return [category.strip() for category in response.split('|')]