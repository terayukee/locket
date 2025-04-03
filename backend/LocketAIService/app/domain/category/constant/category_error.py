from enum import Enum

class CategoryErrorCode(Enum):
    # 2000-2099: 모델 관련 에러
    MODEL_LOAD_ERROR = 2001
    MODEL_PREDICT_ERROR = 2002
    MODEL_NOT_FOUND = 2003

    # 2100-2199: 분류 관련 에러
    CLASSIFICATION_ERROR = 2101
    CLASSIFICATION_VALIDATION_ERROR = 2102
    CLASSIFICATION_PROCESSING_ERROR = 2103

class CategoryErrorMessage(Enum):
    # 모델 관련 메시지
    MODEL_LOAD_ERROR = "모델 로드 중 오류가 발생했습니다"
    MODEL_PREDICT_ERROR = "모델 예측 중 오류가 발생했습니다"
    MODEL_NOT_FOUND = "모델 파일을 찾을 수 없습니다"

    # 분류 관련 메시지
    CLASSIFICATION_ERROR = "카테고리 분류 중 오류가 발생했습니다"
    CLASSIFICATION_VALIDATION_ERROR = "분류 입력값 검증 중 오류가 발생했습니다"
    CLASSIFICATION_PROCESSING_ERROR = "분류 처리 중 오류가 발생했습니다"