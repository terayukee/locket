from typing import List, Dict
from openai import AsyncOpenAI
from app.config.settings import settings
from ..exception.receipt_exception import ReceiptException
from ..constant.receipt_error import ReceiptErrorCode
import logging
import os
import json
import re

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
            self.mappings = self._load_mappings()
        except Exception as e:
            logger.error(f"초기화 실패: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

    def _load_mappings(self) -> Dict[str, str]:
        try:
            file_path = os.path.join(
                os.path.dirname(__file__),
                '../data/item_mappings.json'
            )
            with open(file_path, 'r', encoding='utf-8') as f:
                logger.info("item_mappings.json 로드 성공")
                return json.load(f)
        except Exception as e:
            logger.error(f"item_mappings.json 로드 실패: {str(e)}")
            return {}

    async def classify_receipt(self, receipt_data: Dict) -> Dict:
        try:
            logger.info("품목 분류 시작")
            classified_items = await self._classify_items(receipt_data["items"])
            return {
                'items': classified_items,
                'totalAmount': receipt_data['totalAmount']
            }
        except Exception as e:
            logger.error(f"품목 분류 중 오류 발생: {str(e)}")
            raise ReceiptException(error_code=ReceiptErrorCode.CLASSIFICATION_ERROR)

    async def _classify_items(self, items: List[Dict]) -> List[Dict]:
        mapped_items = []
        unmapped_items = []
        unmapped_indices = []

        for idx, item in enumerate(items):
            original_name = item['itemName'].strip()
            normalized_name = self._normalize_item_name(original_name)
            category = self.mappings.get(normalized_name)

            if category in self.CATEGORIES:
                logger.info(f"매핑 성공: '{original_name}' → '{category}' (정규화: '{normalized_name}')")
                item['itemCategory'] = category
                mapped_items.append(item)
            else:
                logger.info(f"매핑 실패: '{original_name}' (정규화: '{normalized_name}') → LLM 분류 예정")
                unmapped_items.append(original_name)
                unmapped_indices.append(idx)

        # LLM 분류 시도
        if unmapped_items:
            try:
                prompt = self._create_prompt(unmapped_items)
                logger.info(f"AI 요청 프롬프트: {prompt}")

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

                if len(categories) != len(unmapped_items):
                    logger.warning("LLM 응답 수 불일치 - 기타로 fallback")
                    categories = ["기타"] * len(unmapped_items)

            except Exception as e:
                logger.warning(f"LLM 분류 실패: {str(e)} - 모든 항목 기타로 fallback")
                categories = ["기타"] * len(unmapped_items)

            # LLM 결과 병합
            for idx, category in zip(unmapped_indices, categories):
                if category not in self.CATEGORIES:
                    logger.warning(f"유효하지 않은 카테고리 '{category}' → 기타로 fallback")
                    category = "기타"
                items[idx]['itemCategory'] = category
                mapped_items.append(items[idx])

        return mapped_items

    def _normalize_item_name(self, name: str) -> str:
        """상품명 정규화"""
        if not name:
            return ""
        name = name.lower()
        name = re.sub(r"\([^)]*\)", "", name)  # 괄호 제거
        name = re.sub(r"\b\d+\s*(개|병|ml|g|kg|봉|잔|캔|팩|인분|포|조각|L)\b", "", name)  # 단위 제거
        name = re.sub(r"[^\w\s가-힣]", "", name)  # 특수문자 제거
        name = re.sub(r"\s+", " ", name).strip()  # 공백 정리
        return name

    def _create_prompt(self, item_names: List[str]) -> str:
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
        - 설명 없이 카테고리 이름만 출력해주세요
        """

    def _parse_response(self, response: str) -> List[str]:
        response = response.strip()
        if '|' not in response:
            return [response] if response in self.CATEGORIES else ["기타"]

        categories = [cat.strip() for cat in response.split('|')]
        return [c if c in self.CATEGORIES else "기타" for c in categories]
