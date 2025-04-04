from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from py_eureka_client import eureka_client
from starlette.responses import JSONResponse
from datetime import datetime
from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode, CommonErrorMessage

from .api import receipt, category, feedback #, health
import logging
import os

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 환경 변수에서 서비스 설정 읽기
SERVICE_NAME = os.getenv("SERVICE_NAME", "LOCKET-AI-SERVICE")
SERVICE_PORT = int(os.getenv("PORT", 8500))
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")  # 기본값은 로컬
SERVICE_HOST = os.getenv("SERVICE_HOST", "localhost")  # EC2에서는 도메인 주소로 바뀜
ENV = os.getenv("ENV", "prod")  # dev 또는 prod

def create_app() -> FastAPI:
    """FastAPI 애플리케이션 생성 및 설정"""
    app = FastAPI(
        title="Locket AI Service",
        version="1.0.0",
        docs_url="/docs",
        redoc_url="/redoc",
        responses={422: {"model": None}} # 422 에러 코드 사용하지 않으므로 숨김
    )

    # 기존 BaseException 핸들러
    @app.exception_handler(BaseException)
    async def base_exception_handler(request, exc: BaseException):
        return JSONResponse(
            status_code=exc.status_code,
            content={
                "status": exc.status_code,
                "error": exc.error_code,
                "message": exc.message,
                "timestamp": exc.timestamp
            }
        )

    # 새로운 RequestValidationError 핸들러
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        return JSONResponse(
            status_code=400,  # 422 대신 400 사용
            content={
                "status": 400,
                "error": "BAD_REQUEST",
                "message": "잘못된 요청입니다",
                "timestamp": datetime.utcnow().isoformat() + "Z"
            }
        )

    # 처리되지 않은 예외를 위한 핸들러
    @app.exception_handler(Exception)
    async def global_exception_handler(request: Request, exc: Exception):
        logger.error(f"처리되지 않은 예외 발생: {str(exc)}")
        return JSONResponse(
            status_code=StatusCode.INTERNAL_ERROR.value,
            content={
                "status": StatusCode.INTERNAL_ERROR.value,
                "error": "INTERNAL_ERROR",
                "message": CommonErrorMessage.INTERNAL_SERVER_ERROR.value,
                "timestamp": datetime.utcnow().isoformat() + "Z"
            }
        )

    # CORS 설정
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # 라우터 등록
    _register_routers(app)

    # 유레카 등록 (dev 포함 모든 환경에서)
    @app.on_event("startup")
    async def register_to_eureka():
        health_check_url = f"http://{SERVICE_HOST}:{SERVICE_PORT}/health"
        home_page_url = f"http://{SERVICE_HOST}:{SERVICE_PORT}/"
        logger.info(f"📡 Registering {SERVICE_NAME} to Eureka at {EUREKA_SERVER} (env: {ENV})")
        await eureka_client.init_async(
            eureka_server=EUREKA_SERVER,
            app_name=SERVICE_NAME,
            instance_port=SERVICE_PORT,
            instance_host=SERVICE_HOST,
            health_check_url=health_check_url,
            home_page_url=home_page_url,
            renewal_interval_in_secs=10,
            duration_in_secs=30
        )

    return app

def _register_routers(app: FastAPI) -> None:
    routers = [
        (receipt.router, "/api/ai/receipt", "영수증 등록"),
        (category.router, "/api/ai/category", "카테고리 분류"),
        (feedback.router, "/api/ai/feedback", "소비 한 줄 피드백"),
    ]

    for router, prefix, tag in routers:
        app.include_router(router, prefix=prefix, tags=[tag])

# 앱 생성
app = create_app()

# 로컬에서 직접 실행 시
if __name__ == "__main__":
    import uvicorn
    logger.info(f"🚀 Starting {SERVICE_NAME} on port {SERVICE_PORT} (env: {ENV})")
    uvicorn.run("app.main:app", host="0.0.0.0", port=SERVICE_PORT, reload=True)



