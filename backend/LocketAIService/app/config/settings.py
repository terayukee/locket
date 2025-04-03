from pydantic_settings import BaseSettings
from functools import lru_cache

class Settings(BaseSettings):
    # 서비스 환경 설정
    ENV: str = "dev"
    PORT: int = 8000
    SERVICE_NAME: str = "LOCKET-AI-SERVICE"
    SERVICE_HOST: str = "localhost"
    EUREKA_SERVER: str = "http://localhost:8761/eureka"

    # 모델 관련 설정
    MODEL_PATH: str = "app/domain/category/model"
    MODEL_VERSION: str = "v1"

    # API 키 설정
    CLOVA_OCR_URL: str
    CLOVA_OCR_SECRET: str
    OPENAI_API_KEY: str

    # 카테고리 분류 관련 설정
    CATEGORY_CONFIDENCE_THRESHOLD: float = 0.7

    class Config:
        env_file = ".env"
        case_sensitive = True

@lru_cache()
def get_settings() -> Settings:
    return Settings()

settings = get_settings()
