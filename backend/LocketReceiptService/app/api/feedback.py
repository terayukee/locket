from fastapi import APIRouter
import logging
from ..domain.feedback.dto.feedback_dto import FeedbackRequest
from ..domain.feedback.service.feedback_service import FeedbackService
from ..domain.feedback.exception.feedback_exception import FeedbackException

router = APIRouter()
feedback_service = FeedbackService()
logger = logging.getLogger(__name__)

@router.post(
    "/generate",
    response_model=str,
    summary="소비 패턴 피드백 생성",
    description="사용자의 소비 패턴을 분석하여 맞춤형 피드백을 생성합니다."
)
async def generate_feedback(request: FeedbackRequest):
    try:
        # 수신한 데이터 로깅
        logger.info(f"수신한 데이터: categoryAmount={request.categoryAmount}, "
                    f"budgetStatus={request.budgetStatus}, userJob={request.userJob}")

        feedback = await feedback_service.generate_feedback(request)

        # 생성된 피드백 로깅
        logger.info(f"생성된 피드백: {feedback}")

        return feedback
    except Exception as e:
        logger.error(f"피드백 생성 중 오류 발생: {str(e)}")
        raise FeedbackException(f"피드백 생성 중 오류 발생: {str(e)}")