from fastapi import HTTPException
from ..constant.status import StatusCode

class BaseException(HTTPException):
    def __init__(
            self,
            status_code: StatusCode,
            error_code: int,
            message: str,
            detail: str = None
    ):
        super().__init__(
            status_code=status_code.value,
            detail={
                "code": error_code,
                "message": message,
                "detail": detail
            }
        )

    @property
    def error_response(self):
        return {
            "code": self.detail["code"],
            "message": self.detail["message"],
            "detail": self.detail.get("detail")
        }