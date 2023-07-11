package com.sparta.board.service;

import com.sparta.board.dto.*;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.Post;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.PostRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public CommentResponseDto createComment(String tokenValue, Long id, CommentRequestDto requestDto) {

        //작성되 게시글이 있는지 확인
        Post post = findPost(id);
        // jwt 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // jwt 토큰 검증
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("Token Error"); }

        // 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);

        //이름 가져오기
        String username = info.getSubject();

        // RequestDto -> Entity
        Comment comment = new Comment(username,requestDto,post);
        // DB 저장
        Comment saveComment = commentRepository.save(comment);
        // Entity -> ResponseDto
        CommentResponseDto commentResponseDto = new CommentResponseDto(saveComment);
        return commentResponseDto;
    }

    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto, String tokenValue) {

        // 게시글이 존재하는지 확인
        findPost(postId);
        // 댓글이 존재하는지 확인
        Comment comment = findComment(commentId);
        tokenCheck(jwtUtil.substringToken(tokenValue), comment);
        comment.update(commentRequestDto);
        return new CommentResponseDto(comment);
    }

    public ResponseEntity<ResultResponseDto> deleteComment(Long postId, Long commentId, String tokenValue) {
        findPost(postId);
        Comment comment = findComment(commentId);
        tokenCheck(jwtUtil.substringToken(tokenValue), comment);

        commentRepository.delete(comment);
        ResultResponseDto resultResponseDto = new ResultResponseDto("삭제가 완료되었습니다",200);
        return ResponseEntity.ok(resultResponseDto);
    }

    private Post findPost(Long id) {
        // 해당 게시글이 존재하는지 확인
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return post;
    }

    private Comment findComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        return comment;
    }


    // 수정, 삭제 시 jwt 권한 인증 (role 인증까지)
    private void tokenCheck(String token, Comment comment) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
//            ResultResponseDto resultResponseDto = new ResultResponseDto("토큰이 유효하지 않습니다", "400");

    Claims info = jwtUtil.getUserInfoFromToken(token);
        // 이름 가져오기
        String username = info.getSubject();
        // 사용자 권한 가져오기
        String role = info.get("auth", String.class);

        if (!comment.getUsername().equals(username) && role.equals("USER")) {
            throw new IllegalArgumentException("작성자만 삭제/수정할 수 있습니다.");
        }
    }
}
