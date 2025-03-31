from typing import List, Optional
from pydantic import BaseModel

class OrderDetail(BaseModel):
    orderId: str
    cardNumber: str
    amount: int
    paymentOrderStatus: str

class PaymentHistoryDto(BaseModel):
    transactionId: str
    buyerId: int
    sellerId: int
    userJob: str
    birthDate: int
    totalAmount: int
    paymentCategory: Optional[str]
    paymentMerchant: str
    cardId: int
    storeName: str
    receiptUploaded: bool
    paymentStatus: str
    createdAt: str
    orders: List[OrderDetail]
    categoryAmount: dict

class GoalDto(BaseModel):
    goalAmount: int
    goalCategory: Optional[str]
    goalMessage: Optional[str]

class UserDto(BaseModel):
    userId: int
    nickname: str
    birthYear: int
    userJob: str

class FeedbackRequestDto(BaseModel):
    user: UserDto
    goal: Optional[GoalDto]
    payments: List[PaymentHistoryDto]

class FeedbackResponseDto(BaseModel):
    summary: str
    insights: List[str]
    recommendations: List[str]
