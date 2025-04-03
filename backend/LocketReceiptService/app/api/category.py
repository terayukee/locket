from fastapi import APIRouter
from ..domain.category.dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..domain.category.service.category_service import CategoryService
from ..domain.category.exception.category_exception import ClassificationException
import logging

logger = logging.getLogger(__name__)

router = APIRouter()
category_service = CategoryService()

@router.post(
    "/classify",
    response_model=CategoryResponseDto,
    responses={
        200: {
            "description": "조회 성공",
            "content": {
                "application/json": {
                    "example": {
                        "paymentCategory": "카페/디저트",
                        "needItemCheck": False
                    }
                }
            }
        },
        400: {"description": "잘못된 요청"},
        500: {"description": "서버 오류"}
    },
    summary="상호명 기준 카테고리 분류",
    description="결제 상호명을 기반으로 카테고리를 분류하고 품목 분석 필요 여부 반환"
)
async def classify_store(request: CategoryRequestDto):
    logger.info(f"수신한 데이터: storeName={request.storeName}")
    try:
        result = await category_service.classify_store(request)
        logger.info(f"분류 결과: {result}")
        return result
    except ClassificationException as e:
        logger.error(f"분류 중 오류 발생: {str(e)}")
        raise e