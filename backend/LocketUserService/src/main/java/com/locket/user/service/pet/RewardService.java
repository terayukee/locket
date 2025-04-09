package com.locket.user.service.pet;

import com.locket.user.domain.pet.constant.PetConstants;
import com.locket.user.domain.pet.dto.RewardDto;
import com.locket.user.domain.pet.dto.RewardListResponseDto;
import com.locket.user.domain.pet.entity.Reward;
import com.locket.user.domain.pet.repository.CharacterRepository;
import com.locket.user.domain.pet.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.locket.user.domain.pet.entity.Character;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final CharacterRepository characterRepository;

    @Value("${app.image.base-url:/images}")
    private String imageBaseUrl;

    @Transactional
    public RewardDto createReward(Long userId, String rewardName, String characterName) {

        String imageUrl = imageBaseUrl + "/rewards/" + PetConstants.STARBUCKS_AMERICANO_IMAGE_NAME;

        Reward reward = Reward.builder()
                .userId(userId)
                .rewardName(rewardName)
                .characterName(characterName)
                .imageUrl(imageUrl)
                .receivedAt(LocalDateTime.now())
                .build();

        Reward saved = rewardRepository.save(reward);

        return RewardDto.builder()
                .rewardId(saved.getRewardId())
                .rewardName(saved.getRewardName())
                .characterName(saved.getCharacterName())
                .receivedAt(saved.getReceivedAt())
                .build();
    }

    // 유저 보상 목록 조회
    @Transactional(readOnly = true)
    public RewardListResponseDto getUserRewards(Long userId) {
        List<Reward> rewardList = rewardRepository.findTop50ByUserIdOrderByReceivedAtDesc(userId);

        List<RewardDto> dtos = new ArrayList<>();

        for (Reward reward : rewardList) {
            RewardDto dto = RewardDto.builder()
                    .rewardId(reward.getRewardId())
                    .rewardName(reward.getRewardName())
                    .characterName(reward.getCharacterName())
                    .receivedAt(reward.getReceivedAt())
                    // imageUrl 제외
                    .build();
            dtos.add(dto);
        }

        return RewardListResponseDto.builder()
                .rewards(dtos)
                .build();
    }

}