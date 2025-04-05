from fastapi import APIRouter, UploadFile, File

from app.schema.error.model import ErrorResponse
from ..domain.receipt.constant.receipt_error import ReceiptErrorCode
from ..domain.receipt.service.ocr import OCRService
from ..domain.receipt.service.classifier import ItemClassifier
from ..domain.receipt.dto.receipt_dto import ReceiptResponse
from ..common.core.validator import FileValidator
from ..domain.receipt.exception.receipt_exception import ReceiptException
import base64
from io import BytesIO
from pdf2image import convert_from_bytes
import logging

logger = logging.getLogger(__name__)

router = APIRouter()
ocr_service = OCRService()
classifier = ItemClassifier()

async def _process_receipt(image_data: str) -> ReceiptResponse:
    """영수증 이미지 처리 공통 로직"""
    try:
        # OCR 처리
        ocr_result = await ocr_service.extract_text(image_data)
        logger.info(f"OCR 처리 결과: {ocr_result['storeName']}")

        # 품목 분류
        classified_result = await classifier.classify_receipt(ocr_result)
        logger.info(f"품목 분류 결과: {len(classified_result['items'])}개 항목")

        # 할인 금액 처리
        total_sum = sum(item['itemAmount'] for item in classified_result['items'])
        actual_total = classified_result['totalAmount']

        if total_sum > actual_total:
            discount = total_sum - actual_total
            max_amount_item = max(classified_result['items'],
                                  key=lambda x: x['itemAmount'])
            max_amount_item['itemAmount'] -= discount
            logger.info(f"할인 금액 처리: {discount}원")

        # 카테고리별 금액 계산
        category_amount = {}
        for item in classified_result['items']:
            category = item['itemCategory']
            amount = item['itemAmount']
            category_amount[category] = category_amount.get(category, 0) + amount

        logger.info(f"카테고리별 금액: {category_amount}")

        return ReceiptResponse(
            storeName=ocr_result['storeName'],
            items=classified_result['items'],
            totalAmount=actual_total,
            categoryAmount=category_amount
        )

    except ReceiptException:
        raise

@router.post(
    "/camera/{transaction_id}",
    response_model=ReceiptResponse,
    responses={
        200: {
            "description": "영수증 처리 성공",
            "content": {
                "application/json": {
                    "example": {
                        "storeName": "스타벅스",
                        "items": [
                            {
                                "itemId": 1,
                                "itemName": "아메리카노",
                                "itemQuantity": 1,
                                "itemAmount": 4500,
                                "itemCategory": "카페/디저트"
                            }
                        ],
                        "totalAmount": 4500,
                        "categoryAmount": {
                            "카페/디저트": 4500
                        }
                    }
                }
            }
        },
        400: {
            "model": ErrorResponse,
            "description": "잘못된 요청",
            "content": {
                "application/json": {
                    "examples": {
                        "file_not_found": {
                            "summary": "파일 누락",
                            "value": {
                                "status": 400,
                                "error": "FILE_NOT_FOUND",
                                "message": "파일이 없습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "invalid_extension": {
                            "summary": "잘못된 확장자",
                            "value": {
                                "status": 400,
                                "error": "INVALID_EXTENSION",
                                "message": "지원하지 않는 파일 형식입니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "empty_file": {
                            "summary": "빈 파일",
                            "value": {
                                "status": 400,
                                "error": "EMPTY_FILE",
                                "message": "파일이 비어있습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "file_too_large": {
                            "summary": "파일 크기 초과",
                            "value": {
                                "status": 400,
                                "error": "FILE_TOO_LARGE",
                                "message": "파일 크기가 5MB를 초과합니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "low_resolution": {
                            "summary": "낮은 해상도",
                            "value": {
                                "status": 400,
                                "error": "LOW_RESOLUTION",
                                "message": "이미지 해상도가 너무 낮습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "corrupted_file": {
                            "summary": "파일 손상",
                            "value": {
                                "status": 400,
                                "error": "CORRUPTED_FILE",
                                "message": "파일이 손상되었습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        },
        500: {
            "model": ErrorResponse,
            "description": "서버 오류",
            "content": {
                "application/json": {
                    "examples": {
                        "ocr_error": {
                            "summary": "OCR 처리 실패",
                            "value": {
                                "status": 500,
                                "error": "OCR_ERROR",
                                "message": "영수증 인식 처리에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "parsing_error": {
                            "summary": "데이터 파싱 실패",
                            "value": {
                                "status": 500,
                                "error": "PARSING_ERROR",
                                "message": "데이터 추출에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "classification_error": {
                            "summary": "분류 처리 실패",
                            "value": {
                                "status": 500,
                                "error": "CLASSIFICATION_ERROR",
                                "message": "품목 분류에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        }
    },
    summary="영수증 이미지 OCR + 품목 카테고리 분류",
    description="이미지 파일을 OCR 처리하고, 구매 품목 별 카테고리 분류"
)
async def process_receipt_from_camera(
        transaction_id: str,
        file: UploadFile = File(description="영수증 이미지 파일")
):
    logger.info(f"카메라 영수증 처리 시작: transaction_id={transaction_id}")
    try:
        if not file:
            raise ReceiptException(
                error_code=ReceiptErrorCode.FILE_NOT_FOUND,
                detail="파일이 업로드되지 않았습니다"
            )

        # 파일 검증
        await FileValidator.validate_image(file)
        logger.info("이미지 파일 검증 완료")

        # 이미지 처리
        contents = await file.read()
        image_data = base64.b64encode(contents).decode('utf-8')

        return await _process_receipt(image_data)

    except ReceiptException as e:
        logger.error(f"영수증 처리 실패: {str(e)}")
        raise e

@router.post(
    "/pdf/{transaction_id}",
    response_model=ReceiptResponse,
    responses={
        200: {
            "description": "PDF 영수증 처리 성공",
            "content": {
                "application/json": {
                    "example": {
                        "storeName": "스타벅스",
                        "items": [
                            {
                                "itemId": 1,
                                "itemName": "아메리카노",
                                "itemQuantity": 1,
                                "itemAmount": 4500,
                                "itemCategory": "카페/디저트"
                            }
                        ],
                        "totalAmount": 4500,
                        "categoryAmount": {
                            "카페/디저트": 4500
                        }
                    }
                }
            }
        },
        400: {
            "model": ErrorResponse,
            "description": "잘못된 요청",
            "content": {
                "application/json": {
                    "examples": {
                        "file_not_found": {
                            "summary": "파일 누락",
                            "value": {
                                "status": 400,
                                "error": "FILE_NOT_FOUND",
                                "message": "파일이 없습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "invalid_extension": {
                            "summary": "잘못된 확장자",
                            "value": {
                                "status": 400,
                                "error": "INVALID_EXTENSION",
                                "message": "지원하지 않는 파일 형식입니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "empty_file": {
                            "summary": "빈 파일",
                            "value": {
                                "status": 400,
                                "error": "EMPTY_FILE",
                                "message": "파일이 비어있습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "file_too_large": {
                            "summary": "파일 크기 초과",
                            "value": {
                                "status": 400,
                                "error": "FILE_TOO_LARGE",
                                "message": "파일 크기가 5MB를 초과합니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "corrupted_file": {
                            "summary": "파일 손상",
                            "value": {
                                "status": 400,
                                "error": "CORRUPTED_FILE",
                                "message": "파일이 손상되었습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "pdf_too_many_pages": {
                            "summary": "PDF 페이지 초과",
                            "value": {
                                "status": 400,
                                "error": "PDF_TOO_MANY_PAGES",
                                "message": "PDF는 1페이지만 처리 가능합니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        },
        500: {
            "model": ErrorResponse,
            "description": "서버 오류",
            "content": {
                "application/json": {
                    "examples": {
                        "ocr_error": {
                            "summary": "OCR 처리 실패",
                            "value": {
                                "status": 500,
                                "error": "OCR_ERROR",
                                "message": "영수증 인식 처리에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "parsing_error": {
                            "summary": "데이터 파싱 실패",
                            "value": {
                                "status": 500,
                                "error": "PARSING_ERROR",
                                "message": "데이터 추출에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        },
                        "classification_error": {
                            "summary": "분류 처리 실패",
                            "value": {
                                "status": 500,
                                "error": "CLASSIFICATION_ERROR",
                                "message": "품목 분류에 실패했습니다",
                                "timestamp": "2024-03-14T06:30:00.000Z"
                            }
                        }
                    }
                }
            }
        }
    },
    summary="PDF OCR + 품목 카테고리 분류",
    description="거래명세표(PDF)를 OCR 처리하고, 구매 품목 별 카테고리 분류"
)
async def process_receipt_pdf(
        transaction_id: str,
        file: UploadFile = File(description="PDF 거래명세표 파일")
):
    logger.info(f"PDF 거래명세표 처리 시작: transaction_id={transaction_id}")
    try:
        if not file:
            raise ReceiptException(
                error_code=ReceiptErrorCode.FILE_NOT_FOUND,
                detail="파일이 업로드되지 않았습니다"
            )

        # PDF 파일 검증
        await FileValidator.validate_pdf(file)
        logger.info("PDF 파일 검증 완료")

        # PDF를 이미지로 변환
        contents = await file.read()
        images = convert_from_bytes(contents)

        if not images:
            raise ReceiptException(
                error_code=ReceiptErrorCode.CORRUPTED_FILE,
                detail="PDF를 이미지로 변환할 수 없습니다"
            )

        # 첫 페이지만 처리
        first_page = images[0]
        logger.info("PDF 첫 페이지 변환 완료")

        # 이미지를 바이트로 변환
        img_byte_arr = BytesIO()
        first_page.save(img_byte_arr, format='JPEG')
        img_byte_arr = img_byte_arr.getvalue()

        # 이미지 데이터를 base64로 인코딩
        image_data = base64.b64encode(img_byte_arr).decode('utf-8')

        return await _process_receipt(image_data)

    except ReceiptException as e:
        logger.error(f"영수증 처리 실패: {str(e)}")
        raise e