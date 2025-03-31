from ..dto.feedback_dto import FeedbackRequestDto, FeedbackResponseDto
from datetime import datetime
from collections import defaultdict
import statistics

class FeedbackService:
    async def analyze_feedback(self, request: FeedbackRequestDto) -> FeedbackResponseDto:
        payments = request.payments
        user = request.user
        goal = request.goal

        # 🔹 기본 정보
        birth_year = user.birthYear
        age = datetime.now().year - birth_year
        job = user.userJob
        total_spent = sum(p.totalAmount for p in payments)

        # 🔹 통계 정보
        category_stats = {stat.category: stat for stat in request.categoryStats}
        day_of_week_stats = request.dayOfWeekStats.dayOfWeekStats
        card_stats = request.cardStats
        top_store = request.topStoreName
        user_spending = request.userSpending or {}
        age_group_avg = request.ageGroupAverage or {}
        current_total = request.currentMonthTotal or 0
        previous_total = request.previousMonthTotal or 0
        total_change = request.totalChangeRate or 0.0
        category_change = request.categoryChangeRate or {}
        hot_categories = request.hotCategories or []
        entropy = request.entropy or 0.0

        insights = []
        recommendations = []

        # 🔸 1. 과소비 항목 분석
        overspent = [cat for cat, stat in category_stats.items() if stat.ratio > 0.3]
        if overspent:
            insights.append(f"🔥 '{', '.join(overspent)}' 카테고리에 전체 지출의 30% 이상이 몰렸습니다.")

        # 🔸 2. 연령대 평균 소비 비교
        for category, my_amt in user_spending.items():
            avg_amt = age_group_avg.get(category)
            if avg_amt is not None:
                if my_amt > avg_amt:
                    insights.append(f"🧍‍♂️ {category} 항목에서 연령대 평균({avg_amt:,.0f}원)보다 많이 소비했습니다.")
                else:
                    insights.append(f"🧍‍♂️ {category} 항목은 연령대 평균({avg_amt:,.0f}원)보다 적게 사용했습니다.")

        # 🔸 3. 목표 대비 초과 여부
        if goal:
            if goal.goalAmount < total_spent:
                over = total_spent - goal.goalAmount
                rate = over / goal.goalAmount * 100
                insights.append(f"🎯 목표({goal.goalAmount:,}원)를 {rate:.1f}% 초과한 {total_spent:,}원 지출했습니다.")
            else:
                insights.append(f"🎯 목표 지출 {goal.goalAmount:,}원 이하로 소비하였습니다.")

        # 🔸 4. 요일별 소비 패턴
        max_day = max(day_of_week_stats, key=day_of_week_stats.get)
        insights.append(f"📊 '{max_day}'에 지출이 가장 많았습니다.")

        # 🔸 5. 전월 대비 증감 분석
        if previous_total > 0:
            delta = total_change * 100
            trend = "증가" if delta > 0 else "감소"
            insights.append(f"🏷️ 전월 대비 총 지출이 {abs(delta):.1f}% {trend}했습니다.")
            for cat, rate in category_change.items():
                if abs(rate) > 0.2:
                    symbol = "▲" if rate > 0 else "▼"
                    insights.append(f" - {cat} 지출 {symbol} {rate * 100:.1f}%")

        # 🔸 6. 카드 사용 집중도
        most_used_card = max(card_stats, key=lambda c: c.usageCount)
        if most_used_card.usageCount > 0.7 * sum(c.usageCount for c in card_stats):
            insights.append(f"⚖️ '{most_used_card.cardName}' 카드에 소비가 집중되었습니다.")

        # 🔸 7. 자주 간 가게
        if top_store:
            insights.append(f"🏪 가장 많이 간 매장은 '{top_store}'입니다.")

        # 🔸 8. Hot 카테고리
        if hot_categories:
            insights.append(f"🔥 최근 3개월간 소비가 증가한 카테고리: {', '.join(hot_categories)}")

        # 🔸 9. 소비 다양성 지수
        insights.append(f"📈 소비 다양성 지수(Shannon entropy)는 {entropy:.2f}입니다.")

        # 🔸 추천: 절약 가능한 상위 카테고리
        top_categories = sorted(category_stats.values(), key=lambda c: c.amount, reverse=True)[:3]
        for cat in top_categories:
            recommendations.append(f"{cat.category} 항목에서 {cat.amount:,}원 지출하였습니다. 절약 가능성을 검토해보세요.")

        return FeedbackResponseDto(
            summary=f"{user.nickname}님의 이번 달 총 지출은 {total_spent:,}원입니다.",
            insights=insights,
            recommendations=recommendations
        )
