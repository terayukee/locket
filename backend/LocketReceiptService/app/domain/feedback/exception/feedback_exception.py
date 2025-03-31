from fastapi import HTTPException, status

class FeedbackAnalyzeException(HTTPException):
    def __init__(self, message: str = "소비 피드백 분석 중 오류가 발생했습니다.", detail: str = None):
        super().__init__(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={
                "message": message,
                "detail": detail or "예상치 못한 오류가 발생했습니다."
            }
        )
