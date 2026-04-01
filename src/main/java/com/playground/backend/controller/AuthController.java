package com.playground.backend.controller;

import com.playground.backend.dto.AuthTokenDto;
import com.playground.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/github")
    public ResponseEntity<AuthTokenDto> githubLogin(@RequestParam String code) {
        return ResponseEntity.ok(authService.githubLogin(code));
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@RequestAttribute String username) {
        return ResponseEntity.ok(username);
    }
}
