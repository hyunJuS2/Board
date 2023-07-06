package com.sparta.board.repository;

import com.sparta.board.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByOrderByCreatedAtDesc(); // 첫 게시 순서대로
}
