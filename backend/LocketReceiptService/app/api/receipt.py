from fastapi import APIRouter, UploadFile, File, HTTPException
from ..services.ocr import OCRService
from ..schemas.receipt import ReceiptResponse
import base64

router = APIRouter()
ocr_service = OCRService()

@router.post("/camera/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_from_camera(
        payment_id: int,
        file: UploadFile = File(description="영수증 이미지 파일")
):
    try:
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
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.post("/pdf/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_pdf(
        payment_id: int,
        file: UploadFile = File(description="PDF 영수증 파일")
):
    # PDF 영수증 처리
    pass