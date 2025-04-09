from enum import Enum

class ReceiptErrorCode(Enum):
    # 400 에러 (1000-1499)
    FILE_NOT_FOUND = 1001        # 파일 누락
    INVALID_EXTENSION = 1002     # 잘못된 확장자
    EMPTY_FILE = 1003           # 빈 파일
    FILE_TOO_LARGE = 1004       # 파일 크기 초과
    LOW_RESOLUTION = 1005       # 낮은 해상도
    CORRUPTED_FILE = 1006       # 파일 손상
    PDF_TOO_MANY_PAGES = 1007   # PDF 페이지 초과
    AMOUNT_MISMATCH = 1008        # 결제 금액 불일치
    TOTAL_AMOUNT_MISMATCH = 1009  # 총액과 항목 합계 불일치
    PAYMENT_NOT_FOUND = 1010    # 결제 내역 없음
    STORE_NAME_MISMATCH = 1011  # 상호명 불일치

    # 500 에러 (1500-1999)
    OCR_ERROR = 1501            # OCR 처리 실패
    PARSING_ERROR = 1502        # 데이터 파싱 실패
    CLASSIFICATION_ERROR = 1503  # 분류 처리 실패

class ReceiptErrorMessage(Enum):
    # 400 에러 메시지
    FILE_NOT_FOUND = "파일이 없습니다"
    INVALID_EXTENSION = "지원하지 않는 파일 형식입니다"
    EMPTY_FILE = "파일이 비어있습니다"
    FILE_TOO_LARGE = "파일 크기가 5MB를 초과합니다"
    LOW_RESOLUTION = "이미지 해상도가 너무 낮습니다"
    CORRUPTED_FILE = "파일이 손상되었습니다"
    PDF_TOO_MANY_PAGES = "PDF는 1페이지만 처리 가능합니다"
    AMOUNT_MISMATCH = "결제 금액이 영수증 금액과 일치하지 않습니다"
    TOTAL_AMOUNT_MISMATCH = "영수증 총액이 카테고리 합계와 일치하지 않습니다"
    PAYMENT_NOT_FOUND = "해당하는 결제 내역을 찾을 수 없습니다"
    STORE_NAME_MISMATCH = "상호명이 결제 내역과 일치하지 않습니다"

    # 500 에러 메시지
    OCR_ERROR = "영수증 인식 처리에 실패했습니다"
    PARSING_ERROR = "데이터 추출에 실패했습니다"
    CLASSIFICATION_ERROR = "품목 분류에 실패했습니다"