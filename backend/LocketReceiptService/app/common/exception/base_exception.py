from fastapi import HTTPException
from ..constant.status import StatusCode, ErrorMessage

class BaseException(HTTPException):
    def __init__(
            self,
            status_code: StatusCode,
            message: ErrorMessage,
            detail: str = None
    ):
        http_status = 400 if status_code.value < 5000 else 500
        super().__init__(
            status_code=http_status,
            detail={
                "code": status_code.value,
                "message": message.value,
                "detail": detail
            }
        )