package com.locket.user.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.user.domain.auth.dto.KakaoUserInfoDto;
import com.locket.user.exception.KakaoApiException;
import com.locket.user.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoDto getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return parseKakaoUserInfo(response.getBody());

        } catch (HttpStatusCodeException e) {
            int code = e.getStatusCode().value();
            log.error("카카오 API HTTP 오류: {}", code, e);
            switch (code) {
                case 400 -> throw new KakaoApiException("카카오 API 요청이 잘못되었습니다 (400)", HttpStatus.BAD_REQUEST);
                case 401 -> throw new KakaoApiException("카카오 인증 실패 (401)", HttpStatus.UNAUTHORIZED);
                case 500 -> throw new KakaoApiException("카카오 서버 내부 오류 (500)", HttpStatus.INTERNAL_SERVER_ERROR);
                default -> {
                    throw new KakaoApiException(
                            "카카오 API 오류: " + code + " " + HttpStatus.valueOf(code).getReasonPhrase(),
                            HttpStatus.valueOf(code)
                    );
                }
            }
        } catch (RestClientException e) {
            log.error("카카오 API 통신 오류", e);
            throw new UnauthorizedException("카카오 인증에 실패했습니다. 유효하지 않은 토큰입니다.");
        } catch (Exception e) {
            log.error("카카오 사용자 정보 처리 중 오류 발생", e);
            throw new UnauthorizedException("카카오 사용자 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }

    private KakaoUserInfoDto parseKakaoUserInfo(String responseBody) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        long id = Long.parseLong(jsonNode.get("id").asText());

        JsonNode kakaoAccount = jsonNode.get("kakao_account");
        JsonNode profile = kakaoAccount != null && kakaoAccount.has("profile") ? kakaoAccount.get("profile") : null;
        String nickname = (profile != null && profile.has("nickname")) ? profile.get("nickname").asText() : "사용자";

        return KakaoUserInfoDto.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}
