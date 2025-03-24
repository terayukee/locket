from decouple import config

class Settings:
    # config('환경변수명', default='기본값') 형태로 사용
    CLOVA_OCR_URL: str = config('CLOVA_OCR_URL')
    CLOVA_OCR_SECRET: str = config('CLOVA_OCR_SECRET')

settings = Settings()