from openai import AsyncOpenAI
from app.config.settings import settings
from ..dto.feedback_dto import FeedbackRequest
from ..exception.feedback_exception import FeedbackException
import logging

logger = logging.getLogger(__name__)

class FeedbackService:
    def __init__(self):
        self.client = AsyncOpenAI(
            api_key=settings.OPENAI_API_KEY,
            base_url="https://api.openai.com/v1"
        )

    async def generate_feedback(self, request: FeedbackRequest) -> str:
        try:
            prompt = self._create_prompt(request)

            # 생성된 프롬프트 로깅
            logger.info(f"OpenAI에 전송되는 프롬프트: {prompt}")

            response = await self.client.chat.completions.create(
                model="gpt-4",
                messages=[
                    {"role": "system", "content": "당신은 사용자의 소비 패턴을 분석하고 간단한 피드백을 제공하는 전문가입니다."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7,
                max_tokens=100
            )

            feedback = response.choices[0].message.content.strip()
            # OpenAI 응답 로깅
            logger.info(f"OpenAI로부터 받은 응답: {feedback}")

            return feedback

        except Exception as e:
            logger.error(f"피드백 생성 중 오류 발생: {str(e)}")
            raise FeedbackException(f"피드백 생성 중 오류 발생: {str(e)}")

    def _create_prompt(self, request: FeedbackRequest) -> str:
        categories = "\n".join([f"- {k}: {v}원" for k, v in request.categoryAmount.items()])
        budget_info = f"목표 예산: {request.budgetStatus.target}원\n현재 지출: {request.budgetStatus.spent}원"

        return f"""
            사용자 정보:
            - 직업: {request.userJob}
            
            카테고리별 지출:
            {categories}
            
            예산 현황:
            {budget_info}
            
            위 정보를 바탕으로 사용자의 소비 패턴에 대해 15자 내외의 간단한 피드백을 생성해주세요.
            피드백은 격려와 개선점을 모두 포함해야 합니다.
            """