from pydantic import BaseModel
from typing import List, Dict

class ReceiptItem(BaseModel):
    itemId: int
    itemName: str
    itemQuantity: int
    itemAmount: int
    itemCategory: str

class ReceiptResponse(BaseModel):
    storeName: str
    items: List[ReceiptItem]
    totalAmount: int
    categoryAmount: Dict[str, int]