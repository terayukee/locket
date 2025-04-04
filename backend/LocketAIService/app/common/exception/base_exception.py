from fastapi import HTTPException
from datetime import datetime

class BaseException(HTTPException):
    def __init__(self, status_code: int, error_code: str, message: str):
        self.status_code = status_code
        self.error_code = error_code
        self.message = message
        self.timestamp = datetime.now().isoformat()
        super().__init__(status_code=status_code, detail=message)