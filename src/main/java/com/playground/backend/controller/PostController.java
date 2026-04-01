package com.playground.backend.controller;

import com.playground.backend.dto.PostDetailDto;
import com.playground.backend.dto.PostSummaryDto;
import com.playground.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostSummaryDto>> getPosts() {
        return ResponseEntity.ok(postService.getPosts());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<PostDetailDto> getPost(@PathVariable String filename) {
        return ResponseEntity.ok(postService.getPost(filename));
    }
}