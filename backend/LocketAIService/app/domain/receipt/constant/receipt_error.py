from enum import Enum

class ReceiptErrorCode(Enum):
    # 1000-1099: 파일 관련 에러
    FILE_NOT_FOUND = 1001
    FILE_TOO_LARGE = 1002
    INVALID_FILE_TYPE = 1003
    INVALID_IMAGE_SIZE = 1004

    # 1100-1199: OCR 관련 에러
    OCR_PROCESSING_ERROR = 1101
    OCR_PARSING_ERROR = 1102
    OCR_API_ERROR = 1103

    # 1200-1299: 분류 관련 에러
    CLASSIFICATION_INIT_ERROR = 1201
    CLASSIFICATION_ERROR = 1202
    CLASSIFICATION_MISMATCH_ERROR = 1203
    CLASSIFICATION_INVALID_CATEGORY = 1204
    CLASSIFICATION_PARSE_ERROR = 1205

class ReceiptErrorMessage(Enum):
    # 파일 관련 메시지
    FILE_NOT_FOUND = "파일이 업로드되지 않았습니다"
    FILE_TOO_LARGE = "파일 크기가 제한을 초과합니다"
    INVALID_FILE_TYPE = "지원하지 않는 파일 형식입니다"
    INVALID_IMAGE_SIZE = "이미지 해상도가 너무 낮습니다"

    # OCR 관련 메시지
    OCR_PROCESSING_ERROR = "OCR 처리 중 오류가 발생했습니다"
    OCR_PARSING_ERROR = "영수증 데이터 파싱 중 오류가 발생했습니다"
    OCR_API_ERROR = "OCR API 호출 중 오류가 발생했습니다"

    # 분류 관련 메시지
    CLASSIFICATION_INIT_ERROR = "분류 서비스 초기화 중 오류가 발생했습니다"
    CLASSIFICATION_ERROR = "품목 분류 중 오류가 발생했습니다"
    CLASSIFICATION_MISMATCH_ERROR = "품목 수와 카테고리 수가 일치하지 않습니다"
    CLASSIFICATION_INVALID_CATEGORY = "유효하지 않은 카테고리입니다"
    CLASSIFICATION_PARSE_ERROR = "분류 결과 파싱 중 오류가 발생했습니다"