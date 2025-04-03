from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode
from ..constant.receipt_error import ReceiptErrorCode, ReceiptErrorMessage

class FileValidationException(BaseException):
    def __init__(self, error_code: ReceiptErrorCode, detail: str = None):
        super().__init__(
            status_code=StatusCode.BAD_REQUEST,
            error_code=error_code.value,
            message=ReceiptErrorMessage[error_code.name].value,
            detail=detail
        )

class OCRProcessingException(BaseException):
    def __init__(self, error_code: ReceiptErrorCode, detail: str = None):
        super().__init__(
            status_code=StatusCode.INTERNAL_ERROR,
            error_code=error_code.value,
            message=ReceiptErrorMessage[error_code.name].value,
            detail=detail
        )

class ClassificationException(BaseException):
    def __init__(self, error_code: ReceiptErrorCode, detail: str = None):
        super().__init__(
            status_code=StatusCode.INTERNAL_ERROR,
            error_code=error_code.value,
            message=ReceiptErrorMessage[error_code.name].value,
            detail=detail
        )