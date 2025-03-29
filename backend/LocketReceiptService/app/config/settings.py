from decouple import config
from pydantic import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    # 모델 관련 설정
    MODEL_PATH: str = "app/domain/category/model"
    MODEL_VERSION: str = "v1"

    # API 키 설정
    CLOVA_OCR_URL: str = config('CLOVA_OCR_URL')
    CLOVA_OCR_SECRET: str = config('CLOVA_OCR_SECRET')
    OPENAI_API_KEY: str = config('OPENAI_API_KEY')

    # 카테고리 분류 관련 설정
    CATEGORY_CONFIDENCE_THRESHOLD: float = 0.7

    class Config:
        env_file = ".env"
        case_sensitive = True

@lru_cache()
def get_settings() -> Settings:
    """설정 객체를 캐시하여 반환"""
    return Settings()

settings = get_settings()