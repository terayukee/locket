from fastapi import HTTPException
from ..constants.status import StatusCode, ErrorMessage

class ReceiptException(HTTPException):
    def __init__(
            self,
            status_code: StatusCode,
            message: ErrorMessage,
            detail: str = None
    ):
        self.status_code = status_code.value
        self.message = message.value
        super().__init__(
            status_code=self.status_code,
            detail={
                "code": self.status_code,
                "message": self.message,
                "detail": detail
            }
        )

class FileValidationException(ReceiptException):
    def __init__(self, message: ErrorMessage, detail: str = None):
        super().__init__(StatusCode.BAD_REQUEST, message, detail)

class OCRProcessingException(ReceiptException):
    def __init__(self, message: ErrorMessage, detail: str = None):
        super().__init__(StatusCode.OCR_PROCESSING_ERROR, message, detail)