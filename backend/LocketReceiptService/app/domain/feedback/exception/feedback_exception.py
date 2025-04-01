from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode, ErrorMessage

class FeedbackException(BaseException):
    def __init__(self, detail: str = None):
        super().__init__(
            StatusCode.CLASSIFICATION_ERROR,
            ErrorMessage.CLASSIFICATION_ERROR,
            detail
        )