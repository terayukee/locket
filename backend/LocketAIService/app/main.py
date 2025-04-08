from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from py_eureka_client import eureka_client
from starlette.responses import JSONResponse
from datetime import datetime
from app.common.exception.base_exception import BaseException
from app.common.constant.status import StatusCode, CommonErrorMessage
from app.config.settings import settings
from fastapi import APIRouter, Response
from prometheus_client import Counter, generate_latest, CONTENT_TYPE_LATEST

from .api import receipt, category, feedback
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
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")
SERVICE_HOST = os.getenv("SERVICE_HOST", "localhost")
ENV = os.getenv("ENV", "prod")

router = APIRouter()
REQUEST_COUNT = Counter("request_count", "Total request count")

@router.get("/metrics")
def metrics():
    REQUEST_COUNT.inc()
    return Response(generate_latest(), media_type=CONTENT_TYPE_LATEST)

def create_app() -> FastAPI:
    """FastAPI 애플리케이션 생성 및 설정"""

    # Swagger 경로 환경에 따라 분기
    if settings.ENV == "prod":
        docs_url = "/docs"
        redoc_url = "/redoc"
        openapi_url = "/v3/api-docs"  # API Gateway 라우팅에 사용될 경로
    else:
        docs_url = "/docs"
        redoc_url = "/redoc"
        openapi_url = "/openapi.json"  # 로컬 기본 경로

    app = FastAPI(
        title="Locket AI Service",
        version="1.0.0",
        docs_url=docs_url,
        redoc_url=redoc_url,
        openapi_url=openapi_url,
        responses={422: {"model": None}}
    )

    # 예외 핸들러 설정
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

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(request: Request, exc: RequestValidationError):
        return JSONResponse(
            status_code=400,
            content={
                "status": 400,
                "error": "BAD_REQUEST",
                "message": "잘못된 요청입니다",
                "timestamp": datetime.utcnow().isoformat() + "Z"
            }
        )

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

    # CORS
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # 라우터 등록
    _register_routers(app)

    @app.on_event("startup")
    async def startup_event():
        """Eureka에 서비스 등록"""
        try:
            SERVICE_HOST_EXTERNAL = "j12d204.p.ssafy.io"
            SERVICE_PORT_EXTERNAL = os.getenv("PORT", "8500")
            home_page_url = f"http://{SERVICE_HOST_EXTERNAL}:{SERVICE_PORT_EXTERNAL}/"

            logger.info(f"📡 Registering {SERVICE_NAME} to Eureka at {EUREKA_SERVER} (env: {ENV})")
            await eureka_client.init_async(
                eureka_server=EUREKA_SERVER,
                app_name=SERVICE_NAME,
                instance_port=int(SERVICE_PORT_EXTERNAL),
                instance_host=SERVICE_HOST_EXTERNAL,
                home_page_url=home_page_url,
                renewal_interval_in_secs=10,
                duration_in_secs=30
            )
            logger.info("✅ Eureka 등록 성공!")
        except Exception as e:
            logger.error(f"❌ 초기화 실패: {str(e)}")

    return app


def _register_routers(app: FastAPI) -> None:
    from app.api import feedback, category, receipt
    from app import metrics

    routers = [
        (receipt.router, "/api/ai/receipt", "영수증 등록"),
        (category.router, "/api/ai/category", "카테고리 분류"),
        (feedback.router, "/api/ai/feedback", "소비 한 줄 피드백"),
        (metrics.router, "", "Prometheus Metrics")
    ]
    for router, prefix, tag in routers:
        app.include_router(router, prefix=prefix, tags=[tag])

# 앱 실행
app = create_app()

if __name__ == "__main__":
    import uvicorn
    logger.info(f"🚀 Starting {SERVICE_NAME} on port {SERVICE_PORT} (env: {ENV})")
    uvicorn.run("app.main:app", host="0.0.0.0", port=SERVICE_PORT, reload=True)
