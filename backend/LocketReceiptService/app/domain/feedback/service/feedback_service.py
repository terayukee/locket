from ..dto.feedback_dto import FeedbackRequestDto, FeedbackResponseDto
from datetime import datetime
from collections import defaultdict
import statistics

class FeedbackService:
    def analyze_feedback(self, request: FeedbackRequestDto) -> FeedbackResponseDto:
        payments = request.payments
        user = request.user
        goal = request.goal

        # ✅ 총 지출 계산
        total_spent = sum(p.totalAmount for p in payments)
        birth_year = user.birthYear
        age = datetime.now().year - birth_year
        job = user.userJob

        # ✅ 카테고리별 지출 정리
        category_summary = defaultdict(int)
        for p in payments:
            if p.paymentCategory:
                category_summary[p.paymentCategory] += p.totalAmount

        # ✅ 이번 달에 유난히 많이 쓴 카테고리
        overspent = [cat for cat, amt in category_summary.items() if amt > 0.3 * total_spent]

        insights = []
        if overspent:
            insights.append(f"이번 달에는 '{', '.join(overspent)}' 카테고리에서 유독 많은 지출이 있었습니다.")

        # ✅ 목표와 비교
        if goal:
            if goal.goalAmount < total_spent:
                insights.append(f"설정한 목표({goal.goalAmount:,}원)를 초과했습니다. 실제 지출은 {total_spent:,}원입니다.")
            else:
                insights.append(f"목표 지출 {goal.goalAmount:,}원 이하로 소비하였습니다.")

        # ✅ 소비 성향 분석 (ex. 카페, 쇼핑 위주)
        top_categories = sorted(category_summary.items(), key=lambda x: x[1], reverse=True)[:3]
        recommendations = [f"{cat} 항목에서 많이 소비했습니다. 절약할 수 있는 항목인지 점검해보세요." for cat, _ in top_categories]

        # ✅ 나이대 및 직업군 기반 유사 소비자 통계 (데이터셋 필요)
        insights.append(f"당신은 {age}세 {job}입니다. 이와 유사한 사용자들과의 소비 비교를 도입할 수 있습니다. (향후 확장)")

        return FeedbackResponseDto(
            summary=f"{user.nickname}님의 이번 달 총 지출은 {total_spent:,}원입니다.",
            insights=insights,
            recommendations=recommendations
        )
