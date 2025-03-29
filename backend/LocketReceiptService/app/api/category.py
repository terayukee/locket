from fastapi import APIRouter, HTTPException
from ..domain.category.dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..domain.category.service.category_service import CategoryService
from ..domain.category.exception.category_exception import ClassificationException
from ..common.constant.status import ErrorMessage

router = APIRouter()
category_service = CategoryService()

@router.post(
    "/classify",
    response_model=CategoryResponseDto,
    summary="상호명 기준 카테고리 분류",
    description="결제 상호명을 기반으로 카테고리를 분류하고 품목 분석 필요 여부 반환"
)
async def classify_store(request: CategoryRequestDto):
    try:
        return await category_service.classify_store(request)
    except ClassificationException:
        raise
    except Exception as e:
        raise ClassificationException(
            message=ErrorMessage.CLASSIFICATION_ERROR,
            detail=f"카테고리 분류 중 오류 발생: {str(e)}"
        )