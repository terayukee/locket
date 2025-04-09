package com.locket.user.service.pet;

import com.locket.user.domain.pet.constant.PetConstants;
import com.locket.user.domain.pet.dto.*;
import com.locket.user.domain.pet.entity.Character;
import com.locket.user.domain.pet.entity.UserFoodCount;
import com.locket.user.domain.pet.repository.CharacterRepository;
import com.locket.user.domain.pet.repository.UserFoodCountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final RewardService rewardService;
    private final UserFoodCountRepository userFoodCountRepository;
    private final Random random = new Random();

    @Transactional(readOnly = true)
    public PetResponseDto checkPetOwnership(Long userId) {
        boolean hasPet = characterRepository.existsByUserId(userId);
        return PetResponseDto.builder().hasPet(hasPet).build();
    }

    @Transactional(readOnly = true)
    public CharacterInfoDto getCharacterInfo(Long userId) {
        Character character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("캐릭터를 찾을 수 없습니다."));

        // 장난감 사용 가능 여부 확인
        boolean toyAvailable = character.isToyAvailableNow();
        LocalDateTime nextAvailableTime = toyAvailable ? null : character.getNextToyAvailableTime();

        long remainingMinutes = 0;
        if (nextAvailableTime != null) {
            remainingMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), nextAvailableTime);
            if (remainingMinutes < 0) remainingMinutes = 0;
        }

        ToyInfoDto toyInfo = ToyInfoDto.builder()
                .isAvailable(toyAvailable)
                .remainingTimeMinutes(remainingMinutes)
                .build();

        int exp = character.getExp();
        int level = calculateLevel(exp);

        return CharacterInfoDto.builder()
                .characterId(character.getCharacterId())
                .characterName(character.getCharacterName())
                .userId(character.getUserId())
                .level(level)
                .exp(exp)
                .totalExpForNextLevel(calculateTotalExpForNextLevel(level))
                .expPercentage(calculateExpPercentage(exp, level))
                .createdAt(character.getCreatedAt())
                .foodCount(character.getFoodCount())
                .toy(toyInfo)
                .build();
    }

    @Transactional
    public ExpActionResponse addExperience(Long userId, ExpActionRequest request) {
        Character character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("캐릭터를 찾을 수 없습니다."));

        int previousExp = character.getExp();
        boolean levelUp = false;
        int expGained = 0;

        // 액션 타입에 따라 분기
        if (PetConstants.ACTION_TYPE_FEED.equals(request.getActionType())) {
            // 사료 주기 로직
            if (character.getFoodCount() <= 0) {
                throw new IllegalArgumentException("사료가 부족합니다.");
            }
            expGained = PetConstants.FEED_EXP_GAIN;
            character.feedCharacter(expGained);

        } else if (PetConstants.ACTION_TYPE_PLAY.equals(request.getActionType())) {
            // 장난감 사용 로직
            boolean toyAvailable = character.checkAndUpdateToyAvailability();
            if (!toyAvailable) {
                throw new IllegalArgumentException("장난감을 사용할 수 없습니다. 쿨타임을 기다려주세요.");
            }

            expGained = PetConstants.PLAY_EXP_GAIN;
            LocalDateTime nextAvailableTime = LocalDateTime.now().plusMinutes(PetConstants.TOY_COOLDOWN_MINUTES);
            character.playWithCharacter(expGained, nextAvailableTime);

        } else {
            throw new IllegalArgumentException("유효하지 않은 액션 타입입니다.");
        }

        int newExp = character.getExp();
        int level = calculateLevel(newExp);
        double expPercentage = calculateExpPercentage(newExp, level);

        // 레벨업 체크
        if (calculateLevel(previousExp) < level) {
            levelUp = true;
        }

        // 장난감 남은 시간 계산
        long remainingMinutes = 0;
        boolean toyAvailable = character.isToyAvailableNow();

        if (!toyAvailable && character.getNextToyAvailableTime() != null) {
            remainingMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), character.getNextToyAvailableTime());
            if (remainingMinutes < 0) remainingMinutes = 0;
        }

        characterRepository.save(character);

        return ExpActionResponse.builder()
                .characterName(character.getCharacterName())
                .previousExp(previousExp)
                .expGained(expGained)
                .currentExp(newExp)
                .level(level)
                .expPercentage(expPercentage)
                .levelUp(levelUp)
                .toyRemainingTimeMinutes(remainingMinutes)
                .toyAvailable(toyAvailable)
                .build();
    }

    @Transactional
    public CharacterInfoDto createAndReturnCharacter(Long userId) {
        if (characterRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 캐릭터를 보유하고 있습니다.");
        }
        createNewCharacter(userId);
        return getCharacterInfo(userId);
    }

    private Character createNewCharacter(Long userId) {
        int initialFoodCount = userFoodCountRepository.findByUserId(userId)
                .map(foodCount -> foodCount.getFoodCount())
                .orElse(0);
        return createNewCharacter(userId, initialFoodCount);
    }

    private Character createNewCharacter(Long userId, int initialFoodCount) {
        String characterName = generateRandomCharacterName();
        Character character = Character.builder()
                .characterName(characterName)
                .userId(userId)
                .exp(0)
                .createdAt(LocalDateTime.now())
                .foodCount(initialFoodCount)
                .toyAvailable(true)
                .nextToyAvailableTime(null)
                .build();
        return characterRepository.save(character);
    }

    @Transactional
    public RewardDto completeCharacterAndDelete(Long userId) {
        Character character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("캐릭터를 찾을 수 없습니다."));

        String characterName = character.getCharacterName();
        int foodCount = character.getFoodCount();

        // 사료 개수 저장
        userFoodCountRepository.findByUserId(userId).ifPresentOrElse(
                userFoodCount -> {
                    userFoodCount.updateFoodCount(foodCount);
                    userFoodCountRepository.save(userFoodCount);
                },
                () -> {
                    UserFoodCount newUserFoodCount = UserFoodCount.builder()
                            .userId(userId)
                            .foodCount(foodCount)
                            .build();
                    userFoodCountRepository.save(newUserFoodCount);
                }
        );

        // 보상 생성 시 캐릭터 이름 전달
        RewardDto rewardDto = rewardService.createReward(userId, PetConstants.DEFAULT_REWARD, characterName);

        characterRepository.delete(character);

        return rewardDto;
    }


    @Transactional
    public FoodResponseDto addFood(Long userId) {
        Character character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("캐릭터를 찾을 수 없습니다."));

        int previousFoodCount = character.getFoodCount();
        int addedFoodCount = PetConstants.SAMSUNG_CARD_FOOD_BONUS;

        character.addFood(addedFoodCount);
        characterRepository.save(character);

        return FoodResponseDto.builder()
                .previousFoodCount(previousFoodCount)
                .addedFoodCount(addedFoodCount)
                .currentFoodCount(character.getFoodCount())
                .build();
    }

    // 캐릭터 이름 랜덤 생성
    private String generateRandomCharacterName() {
        String personality = PetConstants.PERSONALITY_LIST.get(random.nextInt(PetConstants.PERSONALITY_LIST.size()));
        String adjective = PetConstants.ADJECTIVE_LIST.get(random.nextInt(PetConstants.ADJECTIVE_LIST.size()));
        return personality + " " + adjective + "냥";
    }

    // 레벨 계산
    private int calculateLevel(int exp) {
        for (int i = PetConstants.LEVEL_THRESHOLDS.length - 1; i >= 0; i--) {
            if (exp >= PetConstants.LEVEL_THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    private double calculateExpPercentage(int exp, int level) {
        if (level >= PetConstants.MAX_LEVEL) {
            return 100.0;
        }
        int currentLevelExp = PetConstants.LEVEL_THRESHOLDS[level - 1];
        int nextLevelExp = PetConstants.LEVEL_THRESHOLDS[level];
        int expForCurrentLevel = exp - currentLevelExp;
        int totalExpNeededForNextLevel = nextLevelExp - currentLevelExp;

        return (double) expForCurrentLevel / totalExpNeededForNextLevel * 100;
    }

    private int calculateTotalExpForNextLevel(int level) {
        if (level >= PetConstants.MAX_LEVEL) {
            return 0;
        }
        return PetConstants.LEVEL_THRESHOLDS[level] - PetConstants.LEVEL_THRESHOLDS[level - 1];
    }

    @Transactional
    public CharacterDeleteResponseDto deleteCharacter(Long userId) {
        Character character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("캐릭터를 찾을 수 없습니다."));

        int foodCount = character.getFoodCount();
        userFoodCountRepository.findByUserId(userId).ifPresentOrElse(
                userFoodCount -> {
                    userFoodCount.updateFoodCount(foodCount);
                    userFoodCountRepository.save(userFoodCount);
                },
                () -> {
                    UserFoodCount newUserFoodCount = UserFoodCount.builder()
                            .userId(userId)
                            .foodCount(foodCount)
                            .build();
                    userFoodCountRepository.save(newUserFoodCount);
                }
        );

        characterRepository.delete(character);

        return CharacterDeleteResponseDto.builder()
                .userId(userId)
                .deleted(true)
                .build();
    }
}
