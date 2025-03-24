from fastapi import APIRouter, UploadFile, File
from ..services.ocr import OCRService
from ..services.classifier import ItemClassifier
from ..schemas.receipt import ReceiptResponse
from ..utils.file_validator import FileValidator
from ..exceptions.receipt_exceptions import FileValidationException, OCRProcessingException, ClassificationException
from ..constants.status import ErrorMessage
import base64
from io import BytesIO
from pdf2image import convert_from_bytes

router = APIRouter()
ocr_service = OCRService()
classifier = ItemClassifier()

@router.post("/camera/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_from_camera(
        payment_id: int,
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

        # OCR 결과에 품목 분류 추가
        try:
            classified_result = await classifier.classify_receipt(ocr_result)
        except Exception as e:
            raise ClassificationException(
                message=ErrorMessage.CLASSIFICATION_ERROR,
                detail=str(e)
            )

        return ReceiptResponse(
            payment_id=payment_id,
            store_name=ocr_result['store_name'],
            business_number=ocr_result['business_number'],
            payment_date=ocr_result['payment_date'],
            total_amount=ocr_result['total_amount'],
            items=classified_result['items'],
            category_totals=classified_result.get('category_totals')
        )
    except (FileValidationException, OCRProcessingException, ClassificationException) as e:
        raise e
    except Exception as e:
        raise OCRProcessingException(
            message=ErrorMessage.OCR_PROCESSING_ERROR,
            detail=str(e)
        )

@router.post("/pdf/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_pdf(
        payment_id: int,
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

            try:
                classified_result = await classifier.classify_receipt(ocr_result)
            except Exception as e:
                raise ClassificationException(
                    message=ErrorMessage.CLASSIFICATION_ERROR,
                    detail=str(e)
                )

            return ReceiptResponse(
                payment_id=payment_id,
                store_name=ocr_result['store_name'],
                business_number=ocr_result['business_number'],
                payment_date=ocr_result['payment_date'],
                total_amount=ocr_result['total_amount'],
                items=classified_result['items'],
                category_totals=classified_result.get('category_totals')
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