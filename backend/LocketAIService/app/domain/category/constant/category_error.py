from enum import Enum

class CategoryErrorCode(Enum):
    INVALID_REQUEST = 1400
    MODEL_INIT_ERROR = 1500
    MODEL_LOAD_ERROR = 1501
    MODEL_NOT_FOUND = 1502
    CLASSIFICATION_ERROR = 1503

class CategoryErrorMessage(Enum):
    INVALID_REQUEST = "잘못된 요청입니다"
    MODEL_INIT_ERROR = "모델 초기화에 실패했습니다"
    MODEL_LOAD_ERROR = "모델 로드에 실패했습니다"
    MODEL_NOT_FOUND = "모델 파일을 찾을 수 없습니다"
    CLASSIFICATION_ERROR = "분류 처리 중 오류가 발생했습니다"