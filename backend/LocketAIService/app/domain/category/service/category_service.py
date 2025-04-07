import json
import os
import logging
from typing import Dict
from ..constant.category_const import Category
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import CategoryException
from ..constant.category_error import CategoryErrorCode

logger = logging.getLogger(__name__)

class CategoryService:
    def __init__(self):
        self.mappings = self._load_mappings()

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
            category = self._classify_category(request.storeName)
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

    def _classify_category(self, store_name: str) -> str:
        """상점 분류 로직"""
        try:
            store_name_upper = store_name.upper()

            # 1. 브랜드 매칭
            for brand_key, brand_info in self.mappings['chain_stores'].items():
                # names 배열의 각 이름과 매칭 시도
                if any(name.upper() in store_name_upper for name in brand_info['names']):
                    category = brand_info['category']
                    logger.info(f"브랜드 매칭: {store_name} -> {category}")
                    return category

            # 2. 키워드 매칭
            for category, keywords in self.mappings['category_keywords'].items():
                if any(keyword.upper() in store_name_upper for keyword in keywords):
                    logger.info(f"키워드 매칭: {store_name} -> {category}")
                    return category

            # 3. 매칭 실패시 기타로 분류
            logger.info(f"매칭 실패, 기타로 분류: {store_name}")
            return Category.ETC.value

        except Exception as e:
            logger.error(f"카테고리 분류 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)

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