package com.sparta.board.service;

import com.sparta.board.dto.PostRequestDto;
import com.sparta.board.dto.PostResponseDto;
import com.sparta.board.entity.Post;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.repository.PostRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;

    // 1. 전체 게시글 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    // 2. 선택 게시글 조회
    public PostResponseDto getPost(Long id) {

        // 해당 게시글 찾기
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        //객체에 담아 return
        return new PostResponseDto(post);
    }

    // 3. 게시글 작성
    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto) {

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
        Post post = new Post(username,requestDto);
        // DB 저장
        Post savePost = postRepository.save(post);
        // Entity -> ResponseDto
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }


    public PostResponseDto updatePost(Long id, String tokenValue, PostRequestDto requestDto) {


    }
}
