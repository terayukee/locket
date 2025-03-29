from pydantic import BaseModel, Field

class CategoryRequestDto(BaseModel):
    storeName: str = Field(..., description="결제 상호명")

    class Config:
        json_schema_extra = {
            "example": {
                "storeName": "스타벅스 인동점"
            }
        }

class CategoryResponseDto(BaseModel):
    paymentCategory: str = Field(..., description="분류된 카테고리")
    needItemCheck: bool = Field(..., description="결제 품목 분석 필요 유무")

    class Config:
        json_schema_extra = {
            "example": {
                "paymentCategory": "카페/디저트",
                "needItemCheck": False
            }
        }