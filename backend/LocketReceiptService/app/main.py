from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from py_eureka_client import eureka_client
from .api import receipt, category, feedback
import logging
import os

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

SERVICE_NAME = "LOCKET-AI-SERVICE"
SERVICE_PORT = int(os.getenv("PORT", 8500))
EUREKA_SERVER = os.getenv("EUREKA_SERVER", "http://localhost:8761/eureka")

def create_app() -> FastAPI:
    """FastAPI 애플리케이션 생성 및 설정"""

    app = FastAPI(
        title="Locket AI Service",
        version="1.0.0",
        docs_url="/docs",
        redoc_url="/redoc"
    )

    # CORS 설정
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # 운영 환경에선 특정 origin으로 제한하는 것이 좋음
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # 라우터 등록
    _register_routers(app)

    return app

def _register_routers(app: FastAPI) -> None:
    """API 라우터 등록"""

    routers = [
        (receipt.router, "/receipt", "영수증 등록"),
        (category.router, "/category", "카테고리 분류"),
        (feedback.router, "/feedback", "소비 한 줄 피드백")
    ]

    for router, prefix, tag in routers:
        app.include_router(
            router,
            prefix=prefix,
            tags=[tag]
        )

# FastAPI 앱 생성
app = create_app()

# 유레카 등록
eureka_client.init(
    eureka_server=EUREKA_SERVER,
    app_name=SERVICE_NAME,
    instance_port=SERVICE_PORT,
    instance_host=os.getenv("HOSTNAME", "localhost"),
    health_check_url=f"http://localhost:{SERVICE_PORT}/docs",
    home_page_url=f"http://localhost:{SERVICE_PORT}/",
    prefer_ip=True,
    renewal_interval_in_secs=10,
    duration_in_secs=30
)

if __name__ == "__main__":
    import uvicorn
    logger.info(f"🚀 Starting {SERVICE_NAME} on port {SERVICE_PORT}")
    uvicorn.run("main:app", host="0.0.0.0", port=SERVICE_PORT, reload=True)
