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
        
        아래 조건을 지켜주세요:
        1. 글자 수는 공백 포함 20자 내외를 반드시 지킬 것.
        2. 마지막이 온점으로 끝난다면, 온점은 생략할 것.
        3. 한국어로 작성할 것.
        4. 한 문장으로 작성할 것(너무 길지 않도록).
        5. 지출이 많은 카테고리에 대해, 직접적인 명령 대신 '은근히 제안'하는 방식으로 작성할 것.
        6. 따뜻한 격려나 칭찬은 제외하고, 절약 및 개선 아이디어 위주로 작성할 것.
        7. 문장이 너무 딱딱하지 않게, 부드럽게 표현할 것.
        8. 어투를 반드시 자연스럽게 작성할 것.
        9. 목표 예산 대비 현재 지출 수준을 고려해, 
           - 이미 예산을 초과했거나 근접했다면 '지출 줄이기'를 제안,
           - 아직 여유가 있으면 '소비 조절'을 은근히 제안해 주세요.
        
            예시(너무 길거나 짧을 수 있으니 참고만 하세요):
            - 카페/디저트 예: "오늘 커피는 홈카페 어떠세요?", "가끔은 믹스커피도 맛있답니다"
            - 생활 예: "절약의 시작은 일상에서부터!"
            - 식비 예: "오늘 저녁은 집에서 먹기!"
            - 쇼핑 예: "충동구매는 잠시 쉬어볼까요?", "꼭 필요한 물건인지 생각해보기!"
            - 교통 예: "가까운 거리는 걸어보는 건 어떨까요?", "오늘은 대중교통을 이용하는 하루!"
            - 기타 예: "이번 달 소비 습관 한번 점검해봐요!"
            
            위 정보를 바탕으로, 사용자의 소비 패턴을 고려하여
            위 조건(특히 글자 수 20자 내외)에 맞는
            '한 문장'의 절약 제안 문구를 작성해주세요.
        """

