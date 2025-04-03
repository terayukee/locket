from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode
from ..constant.feedback_error import FeedbackErrorCode, FeedbackErrorMessage

class FeedbackException(BaseException):
    def __init__(self, error_code: FeedbackErrorCode, detail: str = None):
        super().__init__(
            status_code=StatusCode.INTERNAL_ERROR,
            error_code=error_code.value,
            message=FeedbackErrorMessage[error_code.name].value,
            detail=detail
        )

class InvalidRequestException(BaseException):
    def __init__(self, error_code: FeedbackErrorCode, detail: str = None):
        super().__init__(
            status_code=StatusCode.BAD_REQUEST,
            error_code=error_code.value,
            message=FeedbackErrorMessage[error_code.name].value,
            detail=detail
        )