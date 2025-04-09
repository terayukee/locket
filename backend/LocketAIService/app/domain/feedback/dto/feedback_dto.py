from pydantic import BaseModel
from typing import Dict

class BudgetMonthlyStatusDto(BaseModel):
    target: int
    spent: int

class FeedbackRequest(BaseModel):
    categoryAmount: Dict[str, int]
    budgetStatus: BudgetMonthlyStatusDto
    userJob: str

class FeedbackResponse(BaseModel):
    feedback: str