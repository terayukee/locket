from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode
from ..constant.receipt_error import ReceiptErrorCode, ReceiptErrorMessage

class ReceiptException(BaseException):
    def __init__(self, error_code: ReceiptErrorCode):
        status_code = StatusCode.BAD_REQUEST.value if error_code.value < 1500 else StatusCode.INTERNAL_ERROR.value
        super().__init__(
            status_code=status_code,
            error_code=error_code.name,
            message=ReceiptErrorMessage[error_code.name].value
        )