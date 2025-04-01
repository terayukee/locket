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
                model="gpt-4o",
                messages=[
                    {
                        "role": "system",
                        "content": (
                            "당신은 사용자의 예산 현황과 카테고리별 지출을 분석하여, "
                            "짧고 부드러운 제안형 메시지로 절약 아이디어를 주는 전문가입니다."
                        )
                    },
                    {
                        "role": "user",
                        "content": prompt
                    }
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
        """
        요청 객체(FeedbackRequest) 안에는:
          - request.userJob: 사용자 직업
          - request.categoryAmount: {'카페/디저트': 10000, '생활': 20000, ...} 형태
          - request.budgetStatus: BudgetStatus(target=50000, spent=45000 등)
        등의 정보가 들어있다고 가정함
        """
        categories = "\n".join([f"- {k}: {v}원" for k, v in request.categoryAmount.items()])
        budget_info = (
            f"목표 예산: {request.budgetStatus.target}원\n"
            f"현재 지출: {request.budgetStatus.spent}원"
        )

        return f"""
        사용자 정보:
        - 직업: {request.userJob}
        
        카테고리별 지출:
        {categories}
        
        예산 현황:
        {budget_info}
        
        조건:
        1. 글자 수는 공백 포함 20자 내외로 작성하세요.
        2. 문장의 끝에 마침표(.)는 사용하지 마세요.
        3. 한국어만 사용하세요.
        4. 지출이 가장 많은 카테고리를 기준으로, 아래 예시처럼 문장을 작성하세요:
        
        예시:
        - 카페/디저트: 
           - 오늘 커피는 홈카페 어떠세요?
           - 가끔은 믹스커피도 맛있답니다
        - 생활:
           - 절약의 시작은 일상에서부터!
        - 식비:
           - 오늘 저녁은 집에서 먹기!
        - 쇼핑:
           - 충동구매는 잠시 쉬어볼까요?
           - 꼭 필요한 물건인지 생각해보기!
        - 교통:
           - 가까운 거리는 걸어보는 건 어떨까요?
           - 오늘은 대중교통을 이용하는 하루!
        - 기타:
           - 이번 달 소비 습관 한번 점검해봐요!
        
        5. 어투는 자연스럽고 부드럽게 작성하세요.
        6. 예산 현황을 참고하여,
           - 예산 초과 혹은 근접 시: "목표까지 얼마 안남았어요!"처럼 경각심을 주는 문장을 작성
           - 여유가 있다면 지출이 많은 카테고리의 예시 형식을 따라 작성
        7. 목표 예산이 0이라면, "목표 예산을 설정해보세요!"라고 작성
        
        최종 문장은 위의 예시 스타일을 따라, **하나의 짧고 자연스러운 문장**으로 작성하세요.
        """

