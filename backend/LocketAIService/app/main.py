from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from py_eureka_client import eureka_client
from .api import receipt, category, feedback
import logging
import os
import fasttext  # 모델 로딩용

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

# 모델 전역 객체
model = None

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
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # 라우터 등록
    _register_routers(app)

    @app.on_event("startup")
    async def startup_event():
        """유레카 등록 후 모델 로딩"""
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

            logger.info("📦 모델 로딩 시작...")
            global model
            model = fasttext.load_model("/app/app/domain/category/model/category_classifier.bin")
            logger.info("✅ 모델 로딩 완료!")

        except Exception as e:
            logger.error(f"❌ 초기화 실패: {str(e)}")

    return app

def _register_routers(app: FastAPI) -> None:
    routers = [
        (receipt.router, "/api/ai/receipt", "영수증 등록"),
        (category.router, "/api/ai/category", "카테고리 분류"),
        (feedback.router, "/api/ai/feedback", "소비 한 줄 피드백")
    ]
    for router, prefix, tag in routers:
        app.include_router(router, prefix=prefix, tags=[tag])

# 앱 생성
app = create_app()

# 로컬에서 직접 실행할 경우
if __name__ == "__main__":
    import uvicorn
    logger.info(f"🚀 Starting {SERVICE_NAME} on port {SERVICE_PORT} (env: {ENV})")
    uvicorn.run("app.main:app", host="0.0.0.0", port=SERVICE_PORT, reload=True)
