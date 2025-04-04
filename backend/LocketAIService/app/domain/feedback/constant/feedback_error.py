from enum import Enum

class FeedbackErrorCode(Enum):
    # 클라이언트 에러 (3400-3499)
    INVALID_REQUEST = 3400
    MISSING_REQUIRED_FIELD = 3401
    INVALID_PARAMETERS = 3402

    # 서버 에러 (3500-3599)
    GENERATION_ERROR = 3500
    GPT_API_ERROR = 3501
    MODEL_INIT_ERROR = 3502
    CONTENT_VALIDATION_ERROR = 3503

class FeedbackErrorMessage(Enum):
    # 클라이언트 에러 메시지
    INVALID_REQUEST = "잘못된 피드백 요청입니다"
    MISSING_REQUIRED_FIELD = "필수 필드가 누락되었습니다"
    INVALID_PARAMETERS = "잘못된 매개변수입니다"

    # 서버 에러 메시지
    GENERATION_ERROR = "피드백 생성 중 오류가 발생했습니다"
    GPT_API_ERROR = "GPT API 호출 중 오류가 발생했습니다"
    MODEL_INIT_ERROR = "OpenAI 클라이언트 초기화에 실패했습니다"
    CONTENT_VALIDATION_ERROR = "생성된 피드백 검증 중 오류가 발생했습니다"