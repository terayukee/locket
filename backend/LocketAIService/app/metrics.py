from prometheus_client import Counter, generate_latest, CONTENT_TYPE_LATEST
from fastapi import APIRouter, Response
from prometheus_client.core import CollectorRegistry

router = APIRouter()

# 새로운 레지스트리 객체 생성 (충돌 방지용)
custom_registry = CollectorRegistry()
REQUEST_COUNT = Counter("request_count", "Total request count", registry=custom_registry)

@router.get("/metrics")
def metrics():
    REQUEST_COUNT.inc()
    return Response(generate_latest(registry=custom_registry), media_type=CONTENT_TYPE_LATEST)
