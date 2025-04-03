import fasttext
import os
from typing import Optional
from app.config.settings import settings
from ..constant.category_const import Category
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import ModelLoadException, ClassificationException
from ..constant.category_error import CategoryErrorCode
import logging

logger = logging.getLogger(__name__)

class CategoryService:
    def __init__(self):
        try:
            self.MODEL_PATH = settings.MODEL_PATH
            self._load_models()
        except Exception as e:
            logger.error(f"모델 초기화 실패: {str(e)}")
            raise ModelLoadException(
                error_code=CategoryErrorCode.MODEL_LOAD_ERROR,
                detail=f"모델 초기화 실패: {str(e)}"
            )

    def _load_models(self):
        """모델 로드"""
        try:
            logger.info(f"모델 경로: {self.MODEL_PATH}")
            model_files = {
                "category": "category_classifier.bin",
                "item_check": "item_check_classifier.bin"
            }

            self.models = {}
            for key, filename in model_files.items():
                path = os.path.join(self.MODEL_PATH, filename)
                logger.info(f"파일 존재 여부 ({filename}): {os.path.exists(path)}")

                if not os.path.exists(path):
                    raise ModelLoadException(
                        error_code=CategoryErrorCode.MODEL_LOAD_ERROR,
                        detail=f"모델 파일을 찾을 수 없습니다: {path}"
                    )
                try:
                    self.models[key] = fasttext.load_model(path)
                    logger.info(f"{filename} 모델 로드 성공")
                except Exception as e:
                    logger.error(f"모델 로드 실패 ({filename}): {str(e)}")
                    raise ModelLoadException(
                        error_code=CategoryErrorCode.MODEL_LOAD_ERROR,
                        detail=f"모델 로드 실패 ({filename}): {str(e)}"
                    )

        except ModelLoadException:
            raise
        except Exception as e:
            logger.error(f"모델 로드 중 예상치 못한 오류: {str(e)}")
            raise ModelLoadException(
                error_code=CategoryErrorCode.MODEL_LOAD_ERROR,
                detail=f"모델 로드 중 예상치 못한 오류 발생: {str(e)}"
            )

    async def classify_store(self, request: CategoryRequestDto) -> CategoryResponseDto:
        """상점 분류 메인 로직"""
        try:
            if not request.storeName or not request.storeName.strip():
                raise ClassificationException(
                    error_code=CategoryErrorCode.CLASSIFICATION_VALIDATION_ERROR,
                    detail="상호명이 비어있습니다"
                )

            logger.info(f"상점 분류 시작: {request.storeName}")

            # 1. 카테고리 분류
            category = self._classify_category(request.storeName)
            logger.info(f"분류된 카테고리: {category}")

            # 2. 품목 체크 필요 여부 결정
            needs_item_check = self._determine_item_check(
                category=category,
                store_name=request.storeName
            )
            logger.info(f"품목 체크 필요 여부: {needs_item_check}")

            return CategoryResponseDto(
                paymentCategory=category,
                needItemCheck=needs_item_check
            )

        except ClassificationException:
            raise
        except Exception as e:
            logger.error(f"분류 중 예상치 못한 오류: {str(e)}")
            raise ClassificationException(
                error_code=CategoryErrorCode.CLASSIFICATION_ERROR,
                detail=f"분류 중 예상치 못한 오류 발생: {str(e)}"
            )

    def _classify_category(self, store_name: str) -> str:
        """카테고리 분류"""
        try:
            labels, probs = self.models['category'].predict([store_name])
            category = labels[0][0].replace('__label__', '')

            logger.info(f"카테고리 분류 결과 - 상점: {store_name}, 카테고리: {category}, 확률: {probs[0][0]}")

            return category
        except Exception as e:
            logger.error(f"카테고리 분류 실패: {str(e)}")
            raise ClassificationException(
                error_code=CategoryErrorCode.MODEL_PREDICT_ERROR,
                detail=f"카테고리 분류 실패: {str(e)}"
            )

    def _determine_item_check(self, category: str, store_name: str) -> bool:
        """품목 체크 필요 여부 결정"""
        try:
            # 1. 모델 기반 예측
            labels, probs = self.models['item_check'].predict([store_name])
            base_prediction = labels[0][0].replace('__label__', '') == 'True'

            logger.info(f"품목 체크 모델 예측 - 상점: {store_name}, 예측: {base_prediction}, 확률: {probs[0][0]}")

            # 2. 규칙 기반 보정
            # 항상 False인 카테고리들 (품목 체크 불필요)
            if category in [
                Category.CAFE.value,     # 카페/디저트
                Category.LIFE.value,     # 생활
                Category.FOOD.value,     # 식비
                Category.TRANSPORT.value # 교통
            ]:
                logger.info(f"규칙 기반 보정: {category} 카테고리는 항상 False")
                return False

            # 항상 True인 카테고리들 (품목 체크 필요)
            if category in [
                Category.SHOPPING.value, # 쇼핑
                Category.ETC.value      # 기타
            ]:
                logger.info(f"규칙 기반 보정: {category} 카테고리는 항상 True")
                return True

            return base_prediction

        except Exception as e:
            logger.error(f"품목 체크 필요 여부 결정 실패: {str(e)}")
            raise ClassificationException(
                error_code=CategoryErrorCode.CLASSIFICATION_PROCESSING_ERROR,
                detail=f"품목 체크 필요 여부 결정 실패: {str(e)}"
            )