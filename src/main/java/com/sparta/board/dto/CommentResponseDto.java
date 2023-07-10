package com.sparta.board.dto;

import com.sparta.board.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long commentid;
    private String username;
    private String comment;
    private LocalDateTime createdAt;


    public CommentResponseDto(Comment comment) {
        this.commentid = comment.getCommentid();
        this.username = comment.getUsername();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
    }
}
