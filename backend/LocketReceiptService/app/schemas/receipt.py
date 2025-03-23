from pydantic import BaseModel
from typing import List
from decimal import Decimal

class ReceiptItem(BaseModel):
    name: str
    quantity: int
    price: Decimal

class ReceiptResponse(BaseModel):
    payment_id: int
    total_amount: Decimal
    items: List[ReceiptItem]