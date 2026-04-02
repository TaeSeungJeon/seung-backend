package com.playground.backend.service;

import com.playground.backend.dto.GuestbookRequestDto;
import com.playground.backend.dto.GuestbookResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {

    @Value("${github.username}")
    private String username;

    @Value("${github.content-repo}")
    private String contentRepo;

    private final GitHubApiClient gitHubApiClient;

    public List<GuestbookResponseDto> getGuestbook() {
        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues?state=open&labels=guestbook",
                username, contentRepo
        );

        List<Map<String, Object>> issues = gitHubApiClient.get(
                url,
                new ParameterizedTypeReference<>() {
                }
        );

        return issues.stream()
                .map(this::mapToGuestbookResponse)
                .collect(Collectors.toList());
    }

    public GuestbookResponseDto createGuestbook(GuestbookRequestDto req, String author) {
        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues",
                username, contentRepo
        );

        Map<String, Object> body = Map.of(
                "title", req.getTitle(),
                "body", req.getContent(),
                "labels", List.of("guestbook")
        );

        Map<String, Object> issue = gitHubApiClient.post(
                url,
                body,
                new ParameterizedTypeReference<>() {
                }
        );

        return mapToGuestbookResponse(issue);
    }

    public void deleteGuestbook(Long id) {
        String url = String.format(
                "https://api.github.com/repos/%s/%s/issues/%d",
                username, contentRepo, id
        );

        gitHubApiClient.patch(url, Map.of("state", "closed"));
    }

    @SuppressWarnings("unchecked")
    private GuestbookResponseDto mapToGuestbookResponse(Map<String, Object> issue) {
        Map<String, Object> user = (Map<String, Object>) issue.get("user");

        return GuestbookResponseDto.builder()
                .id(((Number) issue.get("number")).longValue())
                .title((String) issue.get("title"))
                .content((String) issue.get("body"))
                .author((String) user.get("login"))
                .avatarUrl((String) user.get("avatar_url"))
                .createdAt((String) issue.get("created_at"))
                .build();
    }
}
