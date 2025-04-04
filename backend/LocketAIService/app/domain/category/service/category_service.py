import fasttext
import os
from typing import Optional
from app.config.settings import settings
from ..constant.category_const import Category
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import CategoryException
from ..constant.category_error import CategoryErrorCode
import logging

logger = logging.getLogger(__name__)

class CategoryService:
    def __init__(self):
        self.models = {}
        self.MODEL_PATH = settings.MODEL_PATH


    def _ensure_model_loaded(self, key: str):
        """모델이 없으면 로드 (lazy loading)"""
        if key in self.models:
            return  # 이미 로드됨

        model_files = {
            "category": "category_classifier.bin",
            "item_check": "item_check_classifier.bin"
        }

        if key not in model_files:
            logger.error(f"알 수 없는 모델 키 요청됨: {key}")
            raise CategoryException(error_code=CategoryErrorCode.MODEL_NOT_FOUND)

        filename = model_files[key]
        path = os.path.join(self.MODEL_PATH, filename)
        logger.info(f"모델 파일 존재 여부 확인 ({filename}): {os.path.exists(path)}")

        if not os.path.exists(path):
            logger.error(f"모델 파일을 찾을 수 없습니다: {path}")
            raise CategoryException(error_code=CategoryErrorCode.MODEL_NOT_FOUND)

        try:
            self.models[key] = fasttext.load_model(path)
            logger.info(f"{filename} 모델 로드 성공")
        except Exception as e:
            logger.error(f"{filename} 모델 로드 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.MODEL_LOAD_ERROR)

    async def classify_store(self, request: CategoryRequestDto) -> CategoryResponseDto:
        """상점 분류 메인 로직"""
        try:
            if not request.storeName or not request.storeName.strip():
                logger.error("상점명이 비어있습니다")
                raise CategoryException(error_code=CategoryErrorCode.INVALID_REQUEST)

            logger.info(f"상점 분류 시작: {request.storeName}")
            category = self._classify_category(request.storeName)
            needs_item_check = self._determine_item_check(category, request.storeName)

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
        try:
            self._ensure_model_loaded("category")
            labels, probs = self.models['category'].predict([store_name])
            category = labels[0][0].replace('__label__', '')
            logger.info(f"카테고리 분류 결과 - 상점: {store_name}, 카테고리: {category}, 확률: {probs[0][0]}")
            return category
        except Exception as e:
            logger.error(f"카테고리 분류 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)

    def _determine_item_check(self, category: str, store_name: str) -> bool:
        try:
            self._ensure_model_loaded("item_check")
            labels, probs = self.models['item_check'].predict([store_name])
            base_prediction = labels[0][0].replace('__label__', '') == 'True'
            logger.info(f"품목 체크 모델 예측 - 상점: {store_name}, 예측: {base_prediction}, 확률: {probs[0][0]}")

            if category in [Category.CAFE.value, Category.LIFE.value, Category.FOOD.value, Category.TRANSPORT.value]:
                logger.info(f"규칙 기반 보정: {category} 카테고리는 항상 False")
                return False

            if category in [Category.SHOPPING.value, Category.ETC.value]:
                logger.info(f"규칙 기반 보정: {category} 카테고리는 항상 True")
                return True

            return base_prediction

        except Exception as e:
            logger.error(f"품목 체크 필요 여부 결정 실패: {str(e)}")
            raise CategoryException(error_code=CategoryErrorCode.CLASSIFICATION_ERROR)
