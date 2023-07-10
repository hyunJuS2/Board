package com.sparta.board.service;

import com.sparta.board.dto.*;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.Post;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.PostRepository;
import com.sparta.board.repository.UserRepository;
import com.sparta.board.service.exception.CustomException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.rmi.server.LogStream.log;

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
            throw new IllegalArgumentException("Token Error");
        }

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

        Comment comment = postAndCommentCheck(postId, commentId);
        tokenCheck(jwtUtil.substringToken(tokenValue), comment);


        comment.update(commentRequestDto);
        return new CommentResponseDto(comment);
    }

    public ResultResponseDto deleteComment(Long postId, Long commentId, String tokenValue) {
        findPost(postId);
        Comment comment = findComment(commentId);
        tokenCheck(jwtUtil.substringToken(tokenValue), comment);

        commentRepository.delete(comment);
        return new ResultResponseDto("삭제가 완료되었습니다.", "200");
    }

    private Post findPost(Long id) {
        // 해당 게시글이 존재하는지 확인
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return post;
    }


    public Comment postAndCommentCheck(Long postId, Long commentId){
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        return comment;
    }

    private String tokenCheck(String token, Comment comment) {
        // jwt 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new CustomException("토큰이 유효하지 않습니다", "400");
        }
        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        String role = info.get("auth", String.class);


        if (!comment.getUsername().equals(username) && role.equals("USER")) {
            throw new CustomException("작성자만 삭제/수정할 수 있습니다.", "400");
        }
        return username;
    }
}
