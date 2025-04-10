from fastapi import APIRouter
from ..domain.category.dto.category_dto import CategoryRequestDto, CategoryResponseDto
from ..domain.category.service.category_service import CategoryService
from ..domain.category.exception.category_exception import CategoryException
from ..schema.error.model import ErrorResponse
from ..schema.error.examples import get_category_error_examples
import logging

logger = logging.getLogger(__name__)

router = APIRouter()
category_service = CategoryService()

@router.post(
    "/classify",
    response_model=CategoryResponseDto,
    responses={
        200: {
            "model": CategoryResponseDto,
            "description": "분류 성공",
            "content": {
                "application/json": {
                    "example": {
                        "paymentCategory": "카페/디저트",
                        "needItemCheck": False
                    }
                }
            }
        },
        400: {
            "model": ErrorResponse,
            "description": "잘못된 요청",
            "content": {
                "application/json": {
                    "examples": {
                        "invalid_request": {
                            "summary": "잘못된 요청",
                            "value": {
                                "status": 400,
                                "error": "INVALID_REQUEST",
                                "message": "잘못된 요청입니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        },
        500: {
            "model": ErrorResponse,
            "description": "서버 오류",
            "content": {
                "application/json": {
                    "examples": {
                        "model_init_error": {
                            "summary": "모델 초기화 실패",
                            "value": {
                                "status": 500,
                                "error": "MODEL_INIT_ERROR",
                                "message": "모델 초기화에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "model_load_error": {
                            "summary": "모델 로드 실패",
                            "value": {
                                "status": 500,
                                "error": "MODEL_LOAD_ERROR",
                                "message": "모델 로드에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "model_not_found": {
                            "summary": "모델 파일 없음",
                            "value": {
                                "status": 500,
                                "error": "MODEL_NOT_FOUND",
                                "message": "모델 파일을 찾을 수 없습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "classification_error": {
                            "summary": "분류 처리 실패",
                            "value": {
                                "status": 500,
                                "error": "CLASSIFICATION_ERROR",
                                "message": "분류 처리 중 오류가 발생했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        }
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
    except CategoryException as e:
        logger.error(f"분류 중 오류 발생: {str(e)}")
        raise e