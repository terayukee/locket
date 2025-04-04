from pydantic import BaseModel
from datetime import datetime

class ErrorResponse(BaseModel):
    status: int
    error: str
    message: str
    timestamp: datetime

    class Config:
        json_schema_extra = {
            "example": {
                "status": 400,
                "error": "BAD_REQUEST",
                "message": "잘못된 요청입니다",
                "timestamp": "2024-04-04T15:43:11.813Z"
            }
        }