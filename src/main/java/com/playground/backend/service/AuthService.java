package com.playground.backend.service;

import com.playground.backend.dto.AuthTokenDto;
import com.playground.backend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final JwtProvider jwtProvider;
    private final RestTemplate restTemplate;

    public AuthTokenDto githubLogin(String code) {
        // 1. code → access_token 교환
        String accessToken = exchangeCodeForToken(code);

        // 2. access_token → 사용자 정보 조회
        Map<String, Object> userInfo = fetchUserInfo(accessToken);

        // 3. JWT 발급
        String username = (String) userInfo.get("login");
        String avatarUrl = (String) userInfo.get("avatar_url");
        String jwt = jwtProvider.generateToken(username);

        return AuthTokenDto.builder()
                .accessToken(jwt)
                .username(username)
                .avatarUrl(avatarUrl)
                .build();
    }

    private String exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        String url = String.format(
                "https://github.com/login/oauth/access_token" +
                        "?client_id=%s&client_secret=%s&code=%s",
                clientId, clientSecret, code
        );

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }
}
