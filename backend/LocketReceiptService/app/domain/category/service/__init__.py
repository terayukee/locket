import joblib
import os
from ..dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..exception.category_exception import ClassificationException

class CategoryService:
    def __init__(self):
        self.MODEL_PATH = "app/models"
        self._load_models()

    def _load_models(self):
        try:
            self.category_model = joblib.load(os.path.join(self.MODEL_PATH, "category_classifier.bin"))
            self.item_check_model = joblib.load(os.path.join(self.MODEL_PATH, "item_check_classifier.bin"))
        except Exception as e:
            raise RuntimeError(f"모델 로드 중 오류 발생: {str(e)}")

    async def classify_store(self, request: CategoryRequestDto) -> CategoryResponseDto:
        # 비즈니스 로직 구현
        if not request.store_name.strip():
            raise ClassificationException(detail="상호명이 비어있습니다.")

        category = self.category_model.predict([request.store_name])[0]
        needs_item_check = bool(self.item_check_model.predict([request.store_name])[0])

        return CategoryResponseDto(
            store_name=request.store_name,
            category=category,
            needs_item_check=needs_item_check
        )