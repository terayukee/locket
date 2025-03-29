import fasttext  # joblib 대신 fasttext import
import os
from typing import Optional
from app.config.settings import settings
from ..constant.category_const import Category
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import ModelLoadException, ClassificationException

class CategoryService:
    def __init__(self):
        self.MODEL_PATH = settings.MODEL_PATH
        self._load_models()

    def _load_models(self):
        """모델 로드"""
        try:
            print(f"모델 경로: {self.MODEL_PATH}")
            model_files = {
                "category": "category_classifier.bin",
                "item_check": "item_check_classifier.bin"
            }

            self.models = {}
            for key, filename in model_files.items():
                path = os.path.join(self.MODEL_PATH, filename)
                print(f"파일 존재 여부 ({filename}): {os.path.exists(path)}")
                if not os.path.exists(path):
                    raise FileNotFoundError(f"모델 파일을 찾을 수 없습니다: {path}")
                try:
                    # fasttext 모델 로드 방식 사용
                    self.models[key] = fasttext.load_model(path)
                except Exception as e:
                    print(f"모델 로드 실패 ({filename}): {str(e)}")
                    raise

        except Exception as e:
            raise ModelLoadException(f"모델 로드 중 오류 발생: {str(e)}")

    async def classify_store(self, request: CategoryRequestDto) -> CategoryResponseDto:
        """상점 분류 메인 로직"""
        try:
            if not request.storeName.strip():
                raise ClassificationException("상호명이 비어있습니다.")

            # 1. 카테고리 분류
            category = self._classify_category(request.storeName)

            # 2. 품목 체크 필요 여부 결정
            needs_item_check = self._determine_item_check(
                category=category,
                store_name=request.storeName
            )

            return CategoryResponseDto(
                paymentCategory=category,
                needItemCheck=needs_item_check
            )

        except ClassificationException:
            raise
        except Exception as e:
            raise ClassificationException(f"분류 중 오류 발생: {str(e)}")

    def _classify_category(self, store_name: str) -> str:
        """카테고리 분류"""
        try:
            # fasttext predict 메서드는 (labels, probabilities) 튜플을 반환
            labels, probs = self.models['category'].predict([store_name])
            # labels[0]에서 첫 번째 예측 레이블을 가져옴
            category = labels[0][0].replace('__label__', '')
            return category
        except Exception as e:
            raise ClassificationException(f"카테고리 분류 실패: {str(e)}")

    def _determine_item_check(self, category: str, store_name: str) -> bool:
        """품목 체크 필요 여부 결정"""
        try:
            # 1. 모델 기반 예측
            labels, probs = self.models['item_check'].predict([store_name])
            # labels[0]에서 첫 번째 예측 레이블을 가져옴
            base_prediction = labels[0][0].replace('__label__', '') == 'True'

            # 2. 규칙 기반 보정 (선택)
            # 항상 False인 카테고리들 (품목 체크 불필요)
            if category in [
                Category.CAFE.value,     # 카페/디저트
                Category.LIFE.value,     # 생활
                Category.FOOD.value,     # 식비
                Category.TRANSPORT.value # 교통
            ]:
                return False

            # 항상 True인 카테고리들 (품목 체크 필요)
            if category in [
                Category.SHOPPING.value, # 쇼핑
                Category.ETC.value      # 기타
            ]:
                return True

            return base_prediction

        except Exception as e:
            raise ClassificationException(f"품목 체크 필요 여부 결정 실패: {str(e)}")