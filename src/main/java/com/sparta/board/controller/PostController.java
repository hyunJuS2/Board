package com.sparta.board.controller;

import com.sparta.board.dto.PostRequestDto;
import com.sparta.board.dto.PostResponseDto;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    //private final JwtUtil jwtUtil;

    // 1. 전체 게시글 조회
    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 2. 선택 게시글 조회
    @GetMapping("/post/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 3. 게시글 작성
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                      @RequestBody PostRequestDto requestDto) {

        return postService.createPost(tokenValue, requestDto);
    }

    // 4. 선택 게시글 수정
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@PathVariable Long id,
                                      @RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                      @RequestBody PostRequestDto requestDto){

        return postService.updatePost(id,tokenValue,requestDto);
    }

}
