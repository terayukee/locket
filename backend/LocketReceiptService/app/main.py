from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .api import receipt, category, feedback
import logging

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

def create_app() -> FastAPI:
    """FastAPI 애플리케이션 생성 및 설정"""

    app = FastAPI(
        title="Locket Receipt Service",
        version="1.0.0",
        docs_url="/docs",
        redoc_url="/api/redoc"
    )

    # CORS 설정
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # 실제 운영 환경에서 구체적인 origin 설정 할 것
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
        (receipt.router, "/api/receipt", "영수증 등록"),
        (category.router, "/api/category", "카테고리 분류"),
        (feedback.router, "/api/feedback", "소비 한 줄 피드백")
    ]

    for router, prefix, tag in routers:
        app.include_router(
            router,
            prefix=prefix,
            tags=[tag]
        )

app = create_app()

if __name__ == "__main__":
    import uvicorn
    logger.info("Starting Locket Receipt Service...")
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)