package com.locket.user.service.auth;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.domain.auth.dto.KakaoUserInfoDto;
import com.locket.user.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    // 카카오 액세스 토큰을 사용하여 사용자 정보를 가져옵니다.
    public KakaoUserInfoDto getUserInfo(String accessToken) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // HTTP 요청 엔티티 생성
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 카카오 API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 응답
            return parseKakaoUserInfo(response.getBody());

        } catch (RestClientException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new UnauthorizedException("카카오 인증에 실패했습니다. 유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            log.error("카카오 사용자 정보 처리 중 오류 발생", e);
            throw new UnauthorizedException("카카오 사용자 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }

    // 카카오 API 응답 DTO로 변환
    private KakaoUserInfoDto parseKakaoUserInfo(String responseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // 필수 값 추출
        String id = jsonNode.get("id").asText();

        // 카카오 계정 정보 추출
        JsonNode kakaoAccount = jsonNode.get("kakao_account");
        String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : null;

        // 프로필 정보 추출

        JsonNode profile = kakaoAccount.has("profile") ? kakaoAccount.get("profile") : null;
        String nickname = (profile != null && profile.has("nickname")) ? profile.get("nickname").asText() : "사용자";

        return KakaoUserInfoDto.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .build();
    }
}