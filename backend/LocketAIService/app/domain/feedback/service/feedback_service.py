from openai import AsyncOpenAI
from app.config.settings import settings
from ..dto.feedback_dto import FeedbackRequest
from ..exception.feedback_exception import FeedbackException
from ..constant.feedback_error import FeedbackErrorCode
import logging

logger = logging.getLogger(__name__)

class FeedbackService:
    def __init__(self):
        try:
            self.client = AsyncOpenAI(
                api_key=settings.OPENAI_API_KEY,
                base_url="https://api.openai.com/v1"
            )
        except Exception as e:
            logger.error(f"OpenAI 클라이언트 초기화 실패: {str(e)}")
            raise FeedbackException(error_code=FeedbackErrorCode.MODEL_INIT_ERROR)

    async def generate_feedback(self, request: FeedbackRequest) -> str:
        try:
            # 필수 필드 검증
            if not request.userJob:
                logger.error("사용자 직업 정보가 없습니다")
                raise FeedbackException(error_code=FeedbackErrorCode.MISSING_REQUIRED_FIELD)

            if not request.categoryAmount or not request.budgetStatus:
                logger.error("예산 데이터가 없습니다")
                raise FeedbackException(error_code=FeedbackErrorCode.MISSING_REQUIRED_FIELD)

            prompt = self._create_prompt(request)
            logger.info(f"OpenAI에 전송되는 프롬프트: {prompt}")

            try:
                response = await self.client.chat.completions.create(
                    model="gpt-4o",
                    messages=[
                        {"role": "system", "content": "당신은 사용자의 예산 현황과 카테고리별 지출을 분석하여, 짧고 부드러운 제안형 메시지로 절약 아이디어를 주는 전문가입니다."},
                        {"role": "user", "content": prompt}
                    ],
                    temperature=0.7,
                    max_tokens=100
                )
            except Exception as e:
                logger.error(f"GPT API 호출 실패: {str(e)}")
                raise FeedbackException(error_code=FeedbackErrorCode.GPT_API_ERROR)

            feedback = response.choices[0].message.content.strip()
            logger.info(f"OpenAI로부터 받은 응답: {feedback}")

            if not feedback:
                logger.error("피드백이 생성되지 않았습니다")
                raise FeedbackException(error_code=FeedbackErrorCode.CONTENT_VALIDATION_ERROR)

            return feedback

        except FeedbackException:
            raise
        except Exception as e:
            logger.error(f"피드백 생성 중 예상치 못한 오류: {str(e)}")
            raise FeedbackException(error_code=FeedbackErrorCode.GENERATION_ERROR)

    def _create_prompt(self, request: FeedbackRequest) -> str:
        try:
            spent = request.budgetStatus.spent
            target = request.budgetStatus.target
            percent = int((spent / target) * 100) if target else 0
            budget_info = (
                f"- 목표 예산: {target:,}원\n"
                f"- 현재 지출: {spent:,}원\n"
                f"- 지출률: {percent}%"
            )

            categories = "\n".join([f"- {cat}: {amount:,}원" for cat, amount in request.categoryAmount.items()])

            return f"""
            사용자 정보:
            - 직업: {request.userJob}
            
            카테고리별 지출:
            {categories}
            
            예산 현황:
            {budget_info}
            
            조건:
            1. 출력은 **공백 포함 20자 내외**의 **한 문장**으로 작성하세요.
            2. 문장은 **자연스럽고 부드러운 한국어 제안형 문장**으로 작성하며, **마침표(.)는 사용하지 마세요**.
            3. 아래 조건을 **순서대로 하나만 판단하여 적용**하세요:
            
            ① 목표 예산이 0원인 경우
            → 아래 예시 스타일을 참고하거나 자유롭게 변형해 주세요:
            - 목표 예산을 설정해보세요!
            - 이번 달 예산, 아직 정하지 않으셨어요?
            - 지출 전에 목표부터 정해보는 건 어때요?
            
            ② 현재 지출이 목표 예산을 초과한 경우
            → 아래 예시 스타일을 참고하거나 자유롭게 변형해 주세요:
            - 목표 예산 초과! 소비를 줄이세요
            - 예산 초과! 정말 아껴야 해요!
            - 이번 달 지출, 매우 위태로워요!
            
            ③ 현재 지출이 목표 예산의 90% 이상인 경우
            → 아래 예시 스타일을 참고하거나 자유롭게 변형해 주세요:
            - 목표 예산 도달이 가까워졌어요
            - 예산이 얼마 안남았어요!
            - 슬슬 예산 마무리할 시점이에요
            
            ④ 위 조건에 모두 해당하지 않는 경우 (즉, 현재 지출이 90% 미만인 경우)
            → **가장 지출이 많은 카테고리의 분위기를 반영하여, 아래 스타일 예시처럼 부드러운 제안형 문장을 창의적으로 작성해 주세요.**
            
            [카테고리 감성 문장 스타일 예시]
            - 카페/디저트:
              - 오늘 커피는 홈카페 어떠세요?
              - 가끔은 믹스커피도 맛있답니다
              - 카페보다는 자판기로 여유를 즐겨봐요
            - 생활:
              - 절약의 시작은 일상에서부터!
              - 무료한 일상도 가끔은 좋답니다
              - 소소한 일상에서도 만족을 느껴보세요
            - 식비:
              - 오늘 저녁은 집에서 먹기!
              - 냉장고 속 음식을 활용하는 하루 어때요?
              - 자취 요리로 하루를 마무리해봐요
              - 식비 아끼며 건강 챙기는 하루!
            - 쇼핑:
              - 오늘은 충동구매 금지데이!
              - 필요한 물건인지 생각해보고 사는 날!
              - 장바구니에 담기 전에 고민부터!
              - 사는 즐거움 대신 고르는 여유를!
            - 교통:
              - 가까운 거리는 걸어보는 건 어떨까요?
              - 오늘은 대중교통을 이용하는 하루!
            - 기타:
              - 소비 습관을 돌아보는 하루!
              - 이번 달 소비 스타일, 점검해보세요!
            
            ※ "지출", "소비", "지갑" 등 모든 카테고리에 공통적으로 해당할 수 있는 표현은 피해주세요.  
            ※ 카테고리 이름은 명시하지 마세요. 대신 해당 감성을 자연스럽게 녹여서 표현해 주세요.  
            ※ 예시는 참고용이며, **동일한 표현이 반복되지 않도록 창의적인 단어, 어조, 시각적 이미지**를 다양하게 사용해 주세요.  
            ※ 누구나 이해할 수 있는 **따뜻하고 친근한 어조**로 작성해 주세요.
            
            4. 반드시 위 조건 중 **하나만 적용**하세요. **두 가지 이상의 조건을 섞지 마세요.**
            """
        except AttributeError as e:
            logger.error(f"잘못된 요청 데이터 형식: {str(e)}")
            raise FeedbackException(error_code=FeedbackErrorCode.INVALID_PARAMETERS)
        except Exception as e:
            logger.error(f"프롬프트 생성 중 오류: {str(e)}")
            raise FeedbackException(error_code=FeedbackErrorCode.GENERATION_ERROR)