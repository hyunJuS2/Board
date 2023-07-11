package com.sparta.board.controller;

import com.sparta.board.dto.*;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.service.CommentService;
import com.sparta.board.service.PostService;
import com.sparta.board.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;


    // 1. 전체 게시글 조회 +) 모든 댓글 조회
    @GetMapping
    public List<PostResponseDto> getPosts() { return postService.getPosts();}

    // 2. 선택 게시글 조회 +) 모든 댓글 조회
    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 3. 게시글 작성
    @PostMapping
    public PostResponseDto createPost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                      @RequestBody PostRequestDto requestDto) {

        return postService.createPost(tokenValue, requestDto);
    }

    // 4. 선택 게시글 수정
    @PutMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable Long id,
                                      @RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                      @RequestBody PostRequestDto requestDto){

        return postService.updatePost(id,tokenValue,requestDto);
    }

    // 5. 선택 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultResponseDto> deletePost(@PathVariable Long id,
                                                        @RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue){

        postService.deletePost(id,tokenValue);
        return ResponseEntity.ok(new ResultResponseDto("삭제가 완료되었습니다.",200));
    }

    // 1. 댓글 작성
    @PostMapping("/{id}/comment")
    public CommentResponseDto createComment(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue,
                                            @PathVariable Long id,
                                            @RequestBody CommentRequestDto requestDto) {

        return commentService.createComment(tokenValue, id, requestDto);
    }

    @PutMapping("/{id}/comment/{commentId}")
    public CommentResponseDto updateComment(
            @PathVariable Long id,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto commentRequestDto,
            @RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue){


        return commentService.updateComment(id, commentId, commentRequestDto, tokenValue);
    }
    @DeleteMapping("/{id}/comment/{commentId}")
    public ResponseEntity<ResultResponseDto> deleteComment(
            @PathVariable Long id,
            @PathVariable Long commentId, //@RequestParam 둘중에 뭐가 더 좋은 지
            @RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {

        return commentService.deleteComment(id, commentId, tokenValue);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<RestApiException> handleException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }
}
