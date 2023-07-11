package com.sparta.board.service;

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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    // 1. 전체 게시글 조회 +) 모든 게시글에 해당하는 모든댓글까지 모두 조회
    public List<PostResponseDto> getPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = new ArrayList<>(); // 값을 담을 리스트 생성

        for (Post post : posts) {
            List<Comment> comments = commentRepository.findAllByPostOrderByCreatedAtDesc(post); //post에 해당하는 comment 값들 가져오기
            List<CommentResponseDto> commentResponseDtosList = new ArrayList<>();

            //여기 CommentResponseDto로 넣어주기 -> 수정시간은 필요없어서 빼야겠음!
            for (Comment comment : comments) {
                CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
                commentResponseDtosList.add(commentResponseDto);
            }
            PostResponseDto responseDto = new PostResponseDto(post, commentResponseDtosList);
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

//    수정시간을 넣어도 상관없다면 아래와 같은 코드로 굳이 Response객체에 담지 않고 comment 객체로 넣어 담아줄 수 있음.

//    public List<PostResponseDto> getPosts() {
//        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
//        List<PostResponseDto> responseDtoList = new ArrayList<>(); // 값을 담을 리스트 생성
//
//        for (Post post : posts) {
//            List<Comment> comments = commentRepository.findAllByPostOrderByCreatedAtDesc(post); //post에 해당하는 comment 값들 가져오기
//            PostResponseDto responseDto = new PostResponseDto(post, comments); //여기 responseDto로 바꾸기 -> 수정시간은 필요없어서 빼야겠음!
//            responseDtoList.add(responseDto);
//        }
//        return responseDtoList;
//    }


    // 2. 선택 게시글 조회 +) 선택한 게시글에 해당하는 댓글까지 모두 조회
    public PostResponseDto getPost(Long id) {

        // 해당 게시글 찾기
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 해당 게시글에 해당하는 댓글 찾기
        List<Comment> comments = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
        // 댓글을 담을 리스트 생성
        List<CommentResponseDto> commentResponseDtosList = new ArrayList<>();

        //CommentResponseDto에 하나씩 담아서 생성한 List에 담아주기
        for (Comment comment : comments) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            commentResponseDtosList.add(commentResponseDto);
        }
        //객체에 담아 return
        return new PostResponseDto(post,commentResponseDtosList);
    }

    // 3. 게시글 작성
    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto) {

        // jwt 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);

        // jwt 토큰 검증
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
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


    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long id, String tokenValue, PostRequestDto requestDto) {

        // 해당 게시글이 존재하는지 확인
        Post post = findPost(id);

        // jwt 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);
        // jwt 토큰 검증
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");}
        // 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        //이름 가져오기
        String username = info.getSubject();

        //게시글의 작성자와 토큰에서 가져온 사용자 정보와 일치하지 않을 때
        if(!post.getUsername().equals(username)){
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");}

        // 게시글 업데이트
        post.update(requestDto);

        return new PostResponseDto(post);
    }

    public void deletePost(Long id, String tokenValue) {

        // 해당 게시글이 존재하는지 확인
        Post post = findPost(id);

        // jwt 토큰 substring
        String token = jwtUtil.substringToken(tokenValue);
        // jwt 토큰 검증
        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");}
        // 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        //이름 가져오기
        String username = info.getSubject();

        //게시글의 작성자와 토큰에서 가져온 사용자 정보와 일치하지 않을 때
        if(!post.getUsername().equals(username)){
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");}

        // 게시글 삭제
        postRepository.delete(post);
    }

    private Post findPost(Long id) {
        // 해당 게시글이 존재하는지 확인
        Post post = postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return post;
    }


}
