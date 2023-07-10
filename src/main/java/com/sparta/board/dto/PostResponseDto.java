package com.sparta.board.dto;

import com.sparta.board.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter

public class PostResponseDto {

    private Long id;
    private String username;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.id = post.getId();
        this.username = post.getUsername();
        this.createdAt = post.getCreatedAt();
    }

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos) { //게시글 조회 시
        this.id = post.getId();
        this.username = post.getUsername();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.contents = post.getContents();
        this.comments = commentResponseDtos;
    }
}
