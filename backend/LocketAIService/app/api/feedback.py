from fastapi import APIRouter
import logging

from ..domain.feedback.constant.feedback_error import FeedbackErrorCode
from ..domain.feedback.dto.feedback_dto import FeedbackRequest, FeedbackResponse
from ..domain.feedback.service.feedback_service import FeedbackService
from ..domain.feedback.exception.feedback_exception import FeedbackException, InvalidRequestException

router = APIRouter()
feedback_service = FeedbackService()
logger = logging.getLogger(__name__)

@router.post(
    "/generate",
    response_model=FeedbackResponse,
    responses={
        200: {
            "description": "피드백 생성 성공",
            "content": {
                "application/json": {
                    "example": {
                        "feedback": "오늘 커피는 홈카페 어떠세요?"
                    }
                }
            }
        },
        400: {"description": "잘못된 요청"},
        500: {"description": "서버 오류"}
    },
    summary="한 줄 소비 피드백 생성",
    description="사용자의 카테고리별 지출과 예산 현황을 기반으로 맞춤형 피드백을 생성합니다"
)
async def generate_feedback(request: FeedbackRequest):
    try:
        # 요청 데이터 로깅 (민감 정보 제외)
        logger.info(
            "피드백 생성 요청 - 직업: %s, 예산현황: %s/%s원 (%.1f%%), 카테고리별 지출: %s",
            request.userJob,
            request.budgetStatus.spent,
            request.budgetStatus.target,
            (request.budgetStatus.spent / request.budgetStatus.target * 100) if request.budgetStatus.target else 0,
            request.categoryAmount
        )

        # 피드백 생성
        feedback = await feedback_service.generate_feedback(request)
        logger.info("생성된 피드백: %s", feedback)

        return FeedbackResponse(feedback=feedback)

    except InvalidRequestException as e:
        logger.warning("잘못된 요청 데이터: %s", str(e))
        raise e
    except FeedbackException as e:
        logger.error("피드백 생성 실패: %s", str(e))
        raise e
    except Exception as e:
        logger.error("예상치 못한 오류 발생: %s", str(e))
        raise FeedbackException(
            error_code=FeedbackErrorCode.GENERATION_ERROR,
            detail=str(e)
        )