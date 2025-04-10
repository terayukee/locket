from fastapi import APIRouter
from ..domain.feedback.dto.feedback_dto import FeedbackRequest, FeedbackResponse
from ..domain.feedback.service.feedback_service import FeedbackService
from ..domain.feedback.exception.feedback_exception import FeedbackException
from ..schema.error.model import ErrorResponse
import logging

router = APIRouter()
feedback_service = FeedbackService()
logger = logging.getLogger(__name__)

@router.post(
    "/generate",
    response_model=FeedbackResponse,
    responses={
        200: {
            "model": FeedbackResponse,
            "description": "피드백 생성 성공",
            "content": {
                "application/json": {
                    "example": {
                        "feedback": "오늘 커피는 홈카페 어떠세요?"
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
                                "message": "잘못된 피드백 요청입니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "missing_required_field": {
                            "summary": "필수 필드 누락",
                            "value": {
                                "status": 400,
                                "error": "MISSING_REQUIRED_FIELD",
                                "message": "필수 필드가 누락되었습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "invalid_parameters": {
                            "summary": "잘못된 매개변수",
                            "value": {
                                "status": 400,
                                "error": "INVALID_PARAMETERS",
                                "message": "잘못된 매개변수입니다",
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
                        "generation_error": {
                            "summary": "피드백 생성 실패",
                            "value": {
                                "status": 500,
                                "error": "GENERATION_ERROR",
                                "message": "피드백 생성 중 오류가 발생했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "gpt_api_error": {
                            "summary": "GPT API 오류",
                            "value": {
                                "status": 500,
                                "error": "GPT_API_ERROR",
                                "message": "GPT API 호출 중 오류가 발생했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "model_init_error": {
                            "summary": "모델 초기화 실패",
                            "value": {
                                "status": 500,
                                "error": "MODEL_INIT_ERROR",
                                "message": "OpenAI 클라이언트 초기화에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "content_validation_error": {
                            "summary": "콘텐츠 검증 실패",
                            "value": {
                                "status": 500,
                                "error": "CONTENT_VALIDATION_ERROR",
                                "message": "생성된 피드백 검증 중 오류가 발생했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        }
    },

    summary="한 줄 소비 피드백 생성",
    description="사용자의 카테고리별 지출과 예산 현황을 기반으로 맞춤형 피드백을 생성합니다"
)
async def generate_feedback(request: FeedbackRequest):
    logger.info(
        "피드백 생성 요청 - 직업: %s, 예산현황: %s/%s원 (%.1f%%), 카테고리별 지출: %s",
        request.userJob,
        request.budgetStatus.spent,
        request.budgetStatus.target,
        (request.budgetStatus.spent / request.budgetStatus.target * 100) if request.budgetStatus.target else 0,
        request.categoryAmount
    )

    result = await feedback_service.generate_feedback(request)
    logger.info("생성된 피드백: %s", result)

    return FeedbackResponse(feedback=result)