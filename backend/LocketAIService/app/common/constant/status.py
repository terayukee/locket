from enum import Enum

class StatusCode(Enum):
    SUCCESS = 200
    CREATED = 201
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    FORBIDDEN = 403
    NOT_FOUND = 404
    INTERNAL_ERROR = 500

class CommonErrorMessage(Enum):
    INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다"
    INVALID_REQUEST = "잘못된 요청입니다"
    UNAUTHORIZED = "인증되지 않은 요청입니다"
    FORBIDDEN = "권한이 없습니다"
    NOT_FOUND = "리소스를 찾을 수 없습니다"