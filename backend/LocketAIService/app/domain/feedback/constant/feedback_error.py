from enum import Enum

class FeedbackErrorCode(Enum):
    # 3000-3099: 피드백 생성 관련 에러
    GENERATION_ERROR = 3001
    INVALID_REQUEST = 3002
    GPT_API_ERROR = 3003
    MISSING_REQUIRED_FIELD = 3004
    CONTENT_VALIDATION_ERROR = 3005
    INVALID_PARAMETERS = 3006

class FeedbackErrorMessage(Enum):
    # 피드백 생성 관련 메시지
    GENERATION_ERROR = "피드백 생성 중 오류가 발생했습니다"
    INVALID_REQUEST = "잘못된 피드백 요청입니다"
    GPT_API_ERROR = "GPT API 호출 중 오류가 발생했습니다"
    MISSING_REQUIRED_FIELD = "필수 필드가 누락되었습니다"
    CONTENT_VALIDATION_ERROR = "생성된 피드백 검증 중 오류가 발생했습니다"
    INVALID_PARAMETERS = "잘못된 매개변수입니다"