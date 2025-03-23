from fastapi import APIRouter, UploadFile, File
from ..services.ocr import OCRService
from ..schemas.receipt import ReceiptResponse

router = APIRouter()
ocr_service = OCRService()

@router.post("/camera/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_from_camera(payment_id: int, image: str):
    # 카메라로 찍은 영수증 처리
    pass

@router.post("/pdf/{payment_id}", response_model=ReceiptResponse)
async def process_receipt_pdf(payment_id: int, file: UploadFile = File(...)):
    # PDF 영수증 처리
    pass