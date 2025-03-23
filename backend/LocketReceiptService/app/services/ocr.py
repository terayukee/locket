import requests
from ..config import settings


class OCRService:
    def __init__(self):
        self.api_url = settings.CLOVA_OCR_URL
        self.secret = settings.CLOVA_OCR_SECRET

    async def extract_text(self, image_data: str):
        # CLOVA OCR API 호출 로직
        pass