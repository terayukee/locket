from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode, ErrorMessage

class CategoryException(BaseException):
    """카테고리 관련 기본 예외"""
    pass

class ModelLoadException(CategoryException):
    """모델 로드 실패 예외"""
    def __init__(self, detail: str = None):
        super().__init__(
            StatusCode.MODEL_LOAD_ERROR,
            ErrorMessage.MODEL_LOAD_ERROR,
            detail
        )

class ClassificationException(CategoryException):
    """분류 실패 예외"""
    def __init__(self, detail: str = None):
        super().__init__(
            StatusCode.CLASSIFICATION_ERROR,
            ErrorMessage.CLASSIFICATION_ERROR,
            detail
        )