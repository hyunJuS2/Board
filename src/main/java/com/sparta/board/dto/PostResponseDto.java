package com.sparta.board.dto;

import com.sparta.board.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter

public class PostResponseDto {

    private Long id;
    private String username;
    private String title;
    private String contents;
    private LocalDateTime createdAt;

    public PostResponseDto(Post post) {
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.id = post.getId();
        this.username = post.getUsername();
        this.createdAt = post.getCreatedAt();
    }
}
