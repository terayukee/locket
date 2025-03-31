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

class FeedbackCategoryStatDto(BaseModel):
    category: str
    amount: float
    ratio: float  # 전체 지출 대비 비율

class FeedbackDayOfWeekDto(BaseModel):
    dayOfWeekStats: dict[str, float]

class FeedbackCardStatDto(BaseModel):
    cardName: str
    usageCount: int
    totalAmount: float


class FeedbackRequestDto(BaseModel):
    user: UserDto
    goal: Optional[GoalDto]
    payments: List[PaymentHistoryDto]

    # 분석용 통계 정보
    categoryStats: List[FeedbackCategoryStatDto]
    dayOfWeekStats: FeedbackDayOfWeekDto
    cardStats: List[FeedbackCardStatDto]

    # 메타 정보
    topStoreName: Optional[str]
    userSpending: Optional[dict[str, float]]
    ageGroupAverage: Optional[dict[str, float]]
    currentMonthTotal: Optional[float]
    previousMonthTotal: Optional[float]
    totalChangeRate: Optional[float]
    categoryChangeRate: Optional[dict[str, float]]
    hotCategories: Optional[List[str]]
    entropy: Optional[float]


class FeedbackResponseDto(BaseModel):
    summary: str
    insights: List[str]
    recommendations: List[str]
