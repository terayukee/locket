from fastapi import FastAPI
from .api import receipt, category
import logging

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)


app = FastAPI(title="Locket Receipt Service")
app.include_router(receipt.router, prefix="/api/receipt", tags=["영수증 등록"])
app.include_router(category.router, prefix="/api/category", tags=["카테고리 분류"])
