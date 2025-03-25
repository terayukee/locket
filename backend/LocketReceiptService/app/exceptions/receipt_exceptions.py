from fastapi import HTTPException
from ..constants.status import StatusCode, ErrorMessage

class ReceiptException(HTTPException):
    def __init__(
            self,
            status_code: StatusCode,
            message: ErrorMessage,
            detail: str = None
    ):
        # HTTP 상태 코드는 400 또는 500을 사용
        http_status = 400 if status_code.value < 5000 else 500

        self.status_code = status_code.value  # 커스텀 에러 코드
        self.message = message.value
        super().__init__(
            status_code=http_status,  # HTTP 상태 코드
            detail={
                "code": self.status_code,  # 커스텀 에러 코드
                "message": self.message,
                "detail": detail
            }
        )

class FileValidationException(ReceiptException):
    def __init__(self, message: ErrorMessage, detail: str = None):
        super().__init__(StatusCode.FILE_NOT_FOUND, message, detail)

class OCRProcessingException(ReceiptException):
    def __init__(self, message: ErrorMessage, detail: str = None):
        super().__init__(StatusCode.OCR_PROCESSING_ERROR, message, detail)

class ClassificationException(ReceiptException):
    def __init__(self, message: ErrorMessage, detail: str = None):
        super().__init__(StatusCode.CLASSIFICATION_ERROR, message, detail)