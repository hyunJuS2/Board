package com.sparta.board.repository;

import com.sparta.board.entity.Comment;
import com.sparta.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //해당 Post에 해당하는 CommentList찾기
    List<Comment> findAllByPostOrderByCreatedAtDesc(Post post);
}
