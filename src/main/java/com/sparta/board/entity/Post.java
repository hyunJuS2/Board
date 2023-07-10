package com.sparta.board.entity;

import com.sparta.board.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post extends Timestamped {
    // 아이디(식별자), 작성자, 제목, 내용, 날짜, 비밀번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "contents", length = 500) //null 값 허용
    private String contents;

    @Column(name = "title") //null 값 허용
    private String title;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    public Post(String username, PostRequestDto requestDto) { // 게시글 등록 생성자
        this.username = username;
        this.contents = requestDto.getContents();
        this.title = requestDto.getTitle();
    }

    public void update(PostRequestDto requestDto) { // 게시글 수정시 생성자
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }
}
