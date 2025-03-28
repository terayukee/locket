package com.locket.user.service.pet;

import com.locket.user.domain.pet.constant.PetConstants;
import com.locket.user.domain.pet.dto.RewardDto;
import com.locket.user.domain.pet.dto.RewardListResponseDto;
import com.locket.user.domain.pet.entity.Reward;
import com.locket.user.domain.pet.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;

    // 이미지 경로가 필요하다면 사용 (예: application.yml 등에 설정)
    @Value("${app.image.base-url:/images}")
    private String imageBaseUrl;

    /**
     * 유저에게 새로운 보상을 생성하고, RewardDto로 반환한다.
     */
    @Transactional
    public RewardDto createReward(Long userId, String rewardName) {
        // 필요한 경우, rewardName에 따라 다른 로직을 추가할 수도 있음
        String imageUrl = imageBaseUrl + "/rewards/" + PetConstants.STARBUCKS_AMERICANO_IMAGE_NAME;

        Reward reward = Reward.builder()
                .userId(userId)
                .rewardName(rewardName)
                .imageUrl(imageUrl)
                .receivedAt(LocalDateTime.now())
                .build();

        Reward saved = rewardRepository.save(reward);

        return RewardDto.builder()
                .rewardId(saved.getRewardId())
                .rewardName(saved.getRewardName())
                .imageUrl(saved.getImageUrl())
                .receivedAt(saved.getReceivedAt())
                .build();
    }

    /**
     * 유저 보상 목록 조회
     */
    @Transactional(readOnly = true)
    public RewardListResponseDto getUserRewards(Long userId) {
        List<Reward> rewardList = rewardRepository.findTop50ByUserIdOrderByReceivedAtDesc(userId);
        List<RewardDto> dtos = rewardList.stream()
                .map(reward -> RewardDto.builder()
                        .rewardId(reward.getRewardId())
                        .rewardName(reward.getRewardName())
                        .imageUrl(reward.getImageUrl())
                        .receivedAt(reward.getReceivedAt())
                        .build())
                .collect(Collectors.toList());

        return RewardListResponseDto.builder()
                .rewards(dtos)
                .build();
    }
}
