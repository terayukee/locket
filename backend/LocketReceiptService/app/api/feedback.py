from fastapi import APIRouter, HTTPException
from ..domain.feedback.dto.feedback_dto import FeedbackRequestDto, FeedbackResponseDto
from ..domain.feedback.service.feedback_service import FeedbackService
from ..domain.feedback.exception.feedback_exception import FeedbackAnalyzeException
from ..common.constant.status import ErrorMessage

router = APIRouter()
feedback_service = FeedbackService()

@router.post(
    "/spending",
    response_model=FeedbackResponseDto,
    summary="소비 패턴 분석",
    description="사용자의 결제 내역, 목표 정보, 개인 정보를 바탕으로 소비 패턴 분석 피드백을 반환합니다."
)
async def analyze_feedback(request: FeedbackRequestDto):
    try:
        print("소비 패턴 분석 시작")
        print("[DEBUG] 📥 request data:", request.model_dump())
        return await feedback_service.analyze_feedback(request)
    except FeedbackAnalyzeException:
        raise
    except Exception as e:
        raise FeedbackAnalyzeException(
            message=ErrorMessage.FEEDBACK_ANALYZE_ERROR.value,
            detail=f"소비 피드백 분석 중 오류 발생: {str(e)}"
        )
