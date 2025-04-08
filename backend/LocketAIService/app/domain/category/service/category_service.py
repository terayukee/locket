import json
import os
import logging
import re
from typing import Dict
from app.config.settings import settings
from openai import AsyncOpenAI

from ..constant.category_const import Category
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import CategoryException
from ..constant.category_error import CategoryErrorCode

logger = logging.getLogger(__name__)


class LLMCategoryClassifier:
    CATEGORIES = ["식비", "카페/디저트", "쇼핑", "생활", "교통", "기타"]

    def __init__(self):
        self.client = AsyncOpenAI(
            api_key=settings.OPENAI_API_KEY,
            base_url="https://api.openai.com/v1"
        )

    async def classify_store_name(self, store_name: str) -> str:
        prompt = self._create_prompt(store_name)
        try:
            response = await self.client.chat.completions.create(
                model="gpt-4o",
                messages=[
                    {"role": "system", "content": "당신은 상호명을 보고 카테고리를 분류하는 전문가입니다."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=20
            )
            category = response.choices[0].message.content.strip()
            if category in self.CATEGORIES:
                return category
            raise ValueError(f"유효하지 않은 카테고리: {category}")
        except Exception as e:
            raise RuntimeError(f"LLM 분류 실패: {str(e)}")

    def _create_prompt(self, store_name: str) -> str:
        cats = ", ".join(self.CATEGORIES)
        return f"""
        다음 상호명을 보고 적절한 소비 카테고리를 분류해주세요.
        
        상호명: {store_name}
        카테고리 후보: {cats}
        
        가장 적절한 카테고리 하나만 선택해서 응답해주세요.
        응답 형식: 카페/디저트
        
        ※ 설명 없이 카테고리 이름만 정확히 응답해주세요.
        """


class CategoryService:
    def __init__(self):
        self.mappings = self._load_mappings()
        self.llm_classifier = LLMCategoryClassifier()

    def _load_mappings(self) -> Dict:
        """매핑 데이터 로드"""
        try:
            file_path = os.path.join(
                os.path.dirname(__file__),
                '../data/store_mappings.json'
            )
            with open(file_path, 'r', encoding='utf-8') as f:
                mappings = json.load(f)
                logger.info("매핑 데이터 로드 성공")
                return mappings
        except Exception as e:
            logger.error(f"매핑 데이터 로드 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)

    async def classify_store(self, request: CategoryRequestDto) -> CategoryResponseDto:
        """상점 분류 메인 로직"""
        try:
            if not request.storeName or not request.storeName.strip():
                logger.error("상점명이 비어있습니다")
                raise CategoryException(error_code=CategoryErrorCode.INVALID_REQUEST)

            logger.info(f"상점 분류 시작: {request.storeName}")
            category = await self._classify_category(request.storeName)
            needs_item_check = self._determine_item_check(category)

            return CategoryResponseDto(
                paymentCategory=category,
                needItemCheck=needs_item_check
            )
        except CategoryException:
            raise
        except Exception as e:
            logger.error(f"분류 중 예상치 못한 오류: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)

    def _normalize_store_name(self, name: str) -> str:
        """상호명 전처리: 괄호 제거, 특수문자 제거, 공백 정리"""
        name = name.upper()
        name = re.sub(r"\([^)]*\)", "", name)       # 괄호 제거
        name = re.sub(r"[^\w\s]", "", name)         # 특수문자 제거
        name = re.sub(r"\s+", " ", name).strip()    # 공백 정리
        return name

    async def _classify_category(self, store_name: str) -> str:
        store_name_upper = self._normalize_store_name(store_name)
        matched_by_brand = False
        matched_by_keyword = False

        # 1. 브랜드 매칭
        for brand_key, brand_info in self.mappings['chain_stores'].items():
            if any(name.upper() in store_name_upper for name in brand_info['names']):
                category = brand_info['category']
                logger.info(f"브랜드 매칭 성공: '{store_name}' → 카테고리 '{category}' (브랜드 키: {brand_key})")
                matched_by_brand = True
                return category

        # 2. 키워드 매칭
        for category, keywords in self.mappings['category_keywords'].items():
            if any(keyword.upper() in store_name_upper for keyword in keywords):
                logger.info(f"키워드 매칭 성공: '{store_name}' → 카테고리 '{category}' (키워드 기반)")
                matched_by_keyword = True
                return category

        # 3. fallback 룰 적용 시도
        fallback_map = {
            "커피": Category.CAFE.value,
            "치킨": Category.FOOD.value,
            "편의점": Category.SHOPPING.value,
            "택시": Category.TRANSPORT.value,
            "렌터카": Category.TRANSPORT.value,
            "헬스": Category.LIFE.value,
            "피자": Category.FOOD.value,
            "브런치": Category.CAFE.value,
            "지하철": Category.TRANSPORT.value,
            "주차장": Category.TRANSPORT.value
        }

        for keyword, fallback_category in fallback_map.items():
            if keyword.upper() in store_name_upper:
                logger.info(f"'기타' fallback 적용 - 키워드 '{keyword}' 발견 → 카테고리 '{fallback_category}'")
                return fallback_category

        # 4. LLM 분류 시도
        try:
            category = await self.llm_classifier.classify_store_name(store_name)
            logger.info(f"LLM fallback 분류 성공: '{store_name}' → '{category}'")
            return category
        except Exception as e:
            logger.warning(f"LLM fallback 실패: '{store_name}', 이유: {e}")

        # 5. 최종 실패 → 기타
        logger.warning(f"카테고리 매칭 실패: '{store_name}' → 기타 (브랜드, 키워드, 룰, LLM 모두 실패)")
        return Category.ETC.value


    def _determine_item_check(self, category: str) -> bool:
        """품목 체크 필요 여부 결정"""
        try:
            # 카테고리별 품목 체크 규칙
            if category in [Category.CAFE.value, Category.LIFE.value,
                            Category.FOOD.value, Category.TRANSPORT.value]:
                logger.info(f"규칙 기반 결정: {category} 카테고리는 품목 체크 불필요")
                return False

            if category in [Category.SHOPPING.value, Category.ETC.value]:
                logger.info(f"규칙 기반 결정: {category} 카테고리는 품목 체크 필요")
                return True

            return False

        except Exception as e:
            logger.error(f"품목 체크 필요 여부 결정 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)