package com.sparta.board.service;

import com.sparta.board.dto.CommentRequestDto;
import com.sparta.board.dto.CommentResponseDto;
import com.sparta.board.dto.PostRequestDto;
import com.sparta.board.dto.PostResponseDto;
import com.sparta.board.entity.Comment;
import com.sparta.board.entity.Post;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.CommentRepository;
import com.sparta.board.repository.PostRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private Post findPost(Long id) {
        // 해당 게시글이 존재하는지 확인
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return post;
    }
}
