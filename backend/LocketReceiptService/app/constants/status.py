from enum import Enum

class StatusCode(Enum):
    # 성공 코드
    SUCCESS = 200
    CREATED = 201

    # 클라이언트 에러
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    NOT_FOUND = 404

    # 파일 관련 에러
    FILE_NOT_FOUND = 4001
    FILE_TOO_LARGE = 4002
    INVALID_FILE_TYPE = 4003
    INVALID_IMAGE_SIZE = 4004

    # OCR 관련 에러
    OCR_PROCESSING_ERROR = 5001
    OCR_PARSING_ERROR = 5002
    CLASSIFICATION_ERROR = 5003

class ErrorMessage(Enum):
    # 파일 관련 메시지
    FILE_NOT_FOUND = "파일이 업로드되지 않았습니다."
    FILE_TOO_LARGE = "파일 크기가 5MB를 초과합니다."
    INVALID_FILE_TYPE = "지원하지 않는 파일 형식입니다."
    INVALID_IMAGE_SIZE = "이미지 해상도가 너무 낮습니다."

    # OCR 관련 메시지
    OCR_PROCESSING_ERROR = "OCR 처리 중 오류가 발생했습니다."
    OCR_PARSING_ERROR = "영수증 데이터 파싱 중 오류가 발생했습니다."
    CLASSIFICATION_ERROR = "품목 분류 중 오류가 발생했습니다."