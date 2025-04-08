from prometheus_client import Counter, generate_latest, CONTENT_TYPE_LATEST
from fastapi import APIRouter, Response

router = APIRouter()

REQUEST_COUNT = Counter("request_count", "Total request count")

@router.get("/metrics")
def metrics():
    REQUEST_COUNT.inc()
    return Response(generate_latest(), media_type=CONTENT_TYPE_LATEST)
