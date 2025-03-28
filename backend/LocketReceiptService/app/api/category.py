from fastapi import APIRouter
from pydantic import BaseModel
from ..services.classifier import ItemClassifier
from ..exceptions.receipt_exceptions import ClassificationException
from ..constants.status import ErrorMessage

router = APIRouter()
classifier = ItemClassifier()

# request body 스키마 정의
class StoreRequest(BaseModel):
    store_name: str

@router.post("/classify")
async def classify_store(request: StoreRequest):
    try:
        result = classifier.classify_store(request.store_name)
        return {
            "store_name": request.store_name,
            **result  # category, confidence, needs_item_check 포함
        }
    except Exception as e:
        raise ClassificationException(
            message=ErrorMessage.CLASSIFICATION_ERROR,
            detail=str(e)
        )