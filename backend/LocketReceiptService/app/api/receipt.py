from fastapi import APIRouter, UploadFile, File, HTTPException
from ..services.ocr import OCRService
from ..schemas.receipt import ReceiptResponse
from ..utils.file_validator import FileValidator
import base64
from io import BytesIO
import json
import requests
from pdf2image import convert_from_bytes

router = APIRouter()
ocr_service = OCRService()

@router.post("/camera/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_from_camera(
        payment_id: int,
        file: UploadFile = File(description="영수증 이미지 파일")
):
    try:
        # 파일이 없는 경우 체크
        if not file:
            raise HTTPException(
                status_code=400,
                detail="파일이 업로드되지 않았습니다."
            )

        # 파일 검증
        await FileValidator.validate_image(file)

        # OCR 처리
        contents = await file.read()
        image_data = base64.b64encode(contents).decode('utf-8')
        ocr_result = await ocr_service.extract_text(image_data)

        return ReceiptResponse(
            payment_id=payment_id,
            store_name=ocr_result['store_name'],
            business_number=ocr_result['business_number'],
            payment_date=ocr_result['payment_date'],
            total_amount=ocr_result['total_amount'],
            items=ocr_result['items']
        )
    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(
            status_code=400,
            detail=f"영수증 처리 중 오류가 발생했습니다: {str(e)}"
        )

@router.post("/pdf/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_pdf(
        payment_id: int,
        file: UploadFile = File(description="PDF 영수증 파일")
):
    try:
        # 파일이 없는 경우 체크
        if not file:
            raise HTTPException(
                status_code=400,
                detail="파일이 업로드되지 않았습니다."
            )

        # PDF 파일 검증
        await FileValidator.validate_pdf(file)

        # PDF 파일 읽기
        contents = await file.read()

        # PDF를 이미지로 변환
        images = convert_from_bytes(contents)
        if not images:
            raise Exception("PDF를 이미지로 변환할 수 없습니다.")

        # 첫 페이지만 처리 (일반적으로 영수증은 1페이지)
        first_page = images[0]

        # 이미지를 바이트로 변환
        img_byte_arr = BytesIO()
        first_page.save(img_byte_arr, format='JPEG')
        img_byte_arr = img_byte_arr.getvalue()

        # 이미지 데이터를 base64로 인코딩
        image_data = base64.b64encode(img_byte_arr).decode('utf-8')

        # OCR 처리 (이미지 형식으로)
        ocr_result = await ocr_service.extract_text(image_data)

        return ReceiptResponse(
            payment_id=payment_id,
            store_name=ocr_result['store_name'],
            business_number=ocr_result['business_number'],
            payment_date=ocr_result['payment_date'],
            total_amount=ocr_result['total_amount'],
            items=ocr_result['items']
        )
    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(
            status_code=400,
            detail=f"영수증 처리 중 오류가 발생했습니다: {str(e)}"
        )