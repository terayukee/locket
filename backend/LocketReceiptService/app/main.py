from fastapi import FastAPI
from .api import receipt
import logging

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)


app = FastAPI(title="Locket Receipt Service")
app.include_router(receipt.router, prefix="/api/receipt", tags=["영수증 등록"])

@app.get("/")
async def root():
    return {"message": "Locket Receipt Service 기본 메세지"}