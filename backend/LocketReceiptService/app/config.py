from pydantic_settings import BaseSettings
from dotenv import load_dotenv
import os

load_dotenv()

class Settings(BaseSettings):
    CLOVA_OCR_URL: str
    CLOVA_OCR_SECRET: str

    class Config:
        env_file = ".env"

settings = Settings()