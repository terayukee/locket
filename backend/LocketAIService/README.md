# Locket Receipt Service

영수증 OCR 처리 및 카테고리 분류 서비스

## 기능
- 영수증 이미지 OCR 처리
- PDF 영수증 처리
- 카테고리 자동 분류

## 설치 및 실행
python

## 가상환경 생성 및 활성화
python -m venv venv
source venv/bin/activate # Windows: .\venv\Scripts\activate

## 의존성 설치
pip install -r requirements.txt

## 서버 실행
uvicorn app.main:app --reload

## API 문서
- Swagger UI: http://localhost:8000/docs