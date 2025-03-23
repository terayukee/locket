from pydantic import BaseModel
from typing import List
from decimal import Decimal

class ReceiptItem(BaseModel):
    name: str
    quantity: int
    price: str

class ReceiptResponse(BaseModel):
    payment_id: int
    store_name: str
    business_number: str
    payment_date: str
    total_amount: str
    items: List[ReceiptItem]