from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode
from ..constant.feedback_error import FeedbackErrorCode, FeedbackErrorMessage

class FeedbackException(BaseException):
    def __init__(self, error_code: FeedbackErrorCode):
        status_code = StatusCode.BAD_REQUEST.value if error_code.value < 3500 else StatusCode.INTERNAL_ERROR.value
        super().__init__(
            status_code=status_code,
            error_code=error_code.name,
            message=FeedbackErrorMessage[error_code.name].value
        )