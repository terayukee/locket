from fastapi import APIRouter, UploadFile, File
from ..domain.receipt.service.ocr import OCRService
from ..domain.receipt.service.classifier import ItemClassifier
from ..domain.receipt.dto.receipt_dto import ReceiptResponse
from ..common.core.validator import FileValidator
from ..domain.receipt.exception.exception import FileValidationException, OCRProcessingException, ClassificationException
from ..common.constant.status import ErrorMessage
import base64
from io import BytesIO
from pdf2image import convert_from_bytes

router = APIRouter()
ocr_service = OCRService()
classifier = ItemClassifier()

@router.post("/camera/{transaction_id}",
             summary="영수증 이미지 OCR + 품목 카테고리 분류",
             response_model=ReceiptResponse)
async def process_receipt_from_camera(
        transaction_id: str,
        file: UploadFile = File(description="영수증 이미지 파일")
):
    try:
        # 파일이 없는 경우 체크
        if not file:
            raise FileValidationException(
                message=ErrorMessage.FILE_NOT_FOUND
            )

        # 파일 검증
        await FileValidator.validate_image(file)

        # OCR 처리
        contents = await file.read()
        image_data = base64.b64encode(contents).decode('utf-8')
        ocr_result = await ocr_service.extract_text(image_data)

        # 품목 분류
        classified_result = await classifier.classify_receipt(ocr_result)

        # 할인 금액 처리
        total_sum = sum(item['itemAmount'] for item in classified_result['items'])
        actual_total = classified_result['totalAmount']

        if total_sum > actual_total:
            discount = total_sum - actual_total
            # 가장 큰 금액의 아이템 찾기
            max_amount_item = max(classified_result['items'],
                                  key=lambda x: x['itemAmount'])
            # 할인 적용
            max_amount_item['itemAmount'] -= discount

        # 카테고리별 금액 계산
        category_amount = {}
        for item in classified_result['items']:
            category = item['itemCategory']
            amount = item['itemAmount']
            category_amount[category] = category_amount.get(category, 0) + amount

        return ReceiptResponse(
            storeName=ocr_result['storeName'],
            items=classified_result['items'],
            totalAmount=actual_total,
            categoryAmount=category_amount
        )

    except (FileValidationException, OCRProcessingException, ClassificationException) as e:
        raise e
    except Exception as e:
        raise OCRProcessingException(
            message=ErrorMessage.OCR_PROCESSING_ERROR,
            detail=str(e)
        )

@router.post("/pdf/{transaction_id}",
             summary="PDF 거래명세표 OCR + 품목 카테고리 분류",
             response_model=ReceiptResponse)
async def process_receipt_pdf(
        transaction_id: str,
        file: UploadFile = File(description="PDF 영수증 파일")
):
    try:
        # 파일이 없는 경우 체크
        if not file:
            raise FileValidationException(
                message=ErrorMessage.FILE_NOT_FOUND
            )

        # PDF 파일 검증
        await FileValidator.validate_pdf(file)

        # PDF 파일 읽기
        contents = await file.read()

        try:
            # PDF를 이미지로 변환
            images = convert_from_bytes(contents)
            if not images:
                raise OCRProcessingException(
                    message=ErrorMessage.OCR_PROCESSING_ERROR,
                    detail="PDF를 이미지로 변환할 수 없습니다."
                )

            # 첫 페이지만 처리
            first_page = images[0]

            # 이미지를 바이트로 변환
            img_byte_arr = BytesIO()
            first_page.save(img_byte_arr, format='JPEG')
            img_byte_arr = img_byte_arr.getvalue()

            # 이미지 데이터를 base64로 인코딩
            image_data = base64.b64encode(img_byte_arr).decode('utf-8')

            # OCR 처리
            ocr_result = await ocr_service.extract_text(image_data)

            # 품목 분류
            classified_result = await classifier.classify_receipt(ocr_result)

            # 할인 금액 처리
            total_sum = sum(item['itemAmount'] for item in classified_result['items'])
            actual_total = classified_result['totalAmount']

            if total_sum > actual_total:
                discount = total_sum - actual_total
                # 가장 큰 금액의 아이템 찾기
                max_amount_item = max(classified_result['items'],
                                      key=lambda x: x['itemAmount'])
                # 할인 적용
                max_amount_item['itemAmount'] -= discount

            # 카테고리별 금액 계산
            category_amount = {}
            for item in classified_result['items']:
                category = item['itemCategory']
                amount = item['itemAmount']
                category_amount[category] = category_amount.get(category, 0) + amount

            return ReceiptResponse(
                storeName=ocr_result['storeName'],
                items=classified_result['items'],
                totalAmount=actual_total,
                categoryAmount=category_amount
            )

        except OCRProcessingException:
            raise
        except Exception as e:
            raise OCRProcessingException(
                message=ErrorMessage.OCR_PROCESSING_ERROR,
                detail=str(e)
            )

    except FileValidationException:
        raise
    except OCRProcessingException:
        raise
    except Exception as e:
        raise OCRProcessingException(
            message=ErrorMessage.OCR_PROCESSING_ERROR,
            detail=str(e)
        )