from pydantic import BaseModel
from typing import List, Optional
from enum import Enum

class Category(str, Enum):
    FOOD = "식비"
    CAFE = "카페/디저트"
    SHOPPING = "쇼핑"
    LIFE = "생활"
    TRANSPORT = "교통"
    ETC = "기타"

class ReceiptItem(BaseModel):
    id: Optional[int] = None
    name: str
    quantity: int
    price: str
    category: Optional[str] = None

class ReceiptItemUpdate(BaseModel):
    name: Optional[str] = None
    quantity: Optional[int] = None
    price: Optional[str] = None
    category: Optional[Category] = None

class ReceiptResponse(BaseModel):
    payment_id: int
    store_name: str
    business_number: str
    payment_date: str
    total_amount: str
    items: List[ReceiptItem]
    category_totals: Optional[dict] = None


