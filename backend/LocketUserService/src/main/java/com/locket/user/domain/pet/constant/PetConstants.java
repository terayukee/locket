package com.locket.user.domain.pet.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PetConstants {

    private PetConstants() {
        throw new AssertionError("상수 클래스는 인스턴스화할 수 없습니다");
    }

    // 레벨별 필요 경험치 (4단계)
    public static final int[] LEVEL_THRESHOLDS = {0, 100, 300, 600};
    public static final int MAX_LEVEL = 4;

    // 경험치 증가량
    public static final int FEED_EXP_GAIN = 10;
    public static final int PLAY_EXP_GAIN = 8;

    // 장난감 쿨타임 (분)
    public static final int TOY_COOLDOWN_MINUTES = 180;

    // 리워드
    public static final String DEFAULT_REWARD = "스타벅스 아메리카노";
    public static final String STARBUCKS_AMERICANO_IMAGE_NAME = "starbucks-americano.png";

    // 액션 타입
    public static final String ACTION_TYPE_FEED = "feed";
    public static final String ACTION_TYPE_PLAY = "play";

    // 삼성카드 결제 시 추가 사료
    public static final int SAMSUNG_CARD_FOOD_BONUS = 1;

    // 캐릭터 이름 생성용
    public static final List<String> PERSONALITY_LIST = Collections.unmodifiableList(Arrays.asList(
            "수줍은", "느긋한", "졸린", "엉뚱한", "소심한", "장난꾸러기", "꿈꾸는", "새침한", "배고픈", "행복한",
            "멍때리는", "애교 많은", "반짝이는", "해맑은", "상냥한", "다정한", "웃음많은", "애정 가득한", "수줍수줍한",
            "귀염둥이", "포근한", "나른한", "몽글몽글한", "포근포근한", "말랑말랑한", "멍한", "구름 같은",
            "평화로운", "차분한", "여유로운", "통통 튀는", "깡충깡충한", "톡톡 튀는", "신나는", "방긋 웃는",
            "기운찬", "반짝반짝한", "열정 가득한", "들뜬", "유쾌한"
    ));

    public static final List<String> ADJECTIVE_LIST = Collections.unmodifiableList(Arrays.asList(
            "말랑", "몽실", "토실", "폭신", "꼬질", "뽀짝", "꾸덕", "보들", "말캉", "살랑", "소복", "반짝",
            "몽글", "노곤", "촉촉", "나른", "포동", "통통", "도톰", "사르르", "말캉", "부들", "포근",
            "말랑", "달달", "시크"
    ));
}
