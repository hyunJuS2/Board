package com.sparta.board.dto;

import com.sparta.board.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String username;
    private String comment;
    private LocalDateTime createdAt;


    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUsername();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }
}
