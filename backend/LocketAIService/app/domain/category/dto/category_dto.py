from pydantic import BaseModel, Field

class CategoryRequestDto(BaseModel):
    storeName: str = Field(..., description="상점 이름")

class CategoryResponseDto(BaseModel):
    paymentCategory: str = Field(..., description="결제 카테고리")
    needItemCheck: bool = Field(..., description="품목 체크 필요 여부")