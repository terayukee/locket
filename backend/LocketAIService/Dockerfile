# ---- Base image ----
FROM python:3.10-slim

# ---- Environment variables ----
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=8500 \
    EUREKA_SERVER=http://eureka-server:8761/eureka \
    SERVICE_NAME=LOCKET-AI-SERVICE

# ---- Working directory ----
WORKDIR /app

# ---- Install dependencies ----
COPY requirements.txt .
RUN apt-get update && apt-get install -y curl iputils-ping procps && \
    pip install --upgrade pip && \
    pip install -r requirements.txt && \
    apt-get clean

# ---- Copy application code ----
COPY . .

# ---- Expose port ----
EXPOSE ${PORT}

# ---- HEALTHCHECK ----
#HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
#  CMD curl -f http://localhost:8500/health || exit 1

# ---- Start application ----
#CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8500"]
CMD ["sh", "-c", "uvicorn app.main:app --host 0.0.0.0 --port $PORT"]
