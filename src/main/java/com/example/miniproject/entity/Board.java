package com.example.miniproject.entity;

import com.example.miniproject.dto.BoardRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private int star;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String placename;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String season;

    @ManyToOne
    private User user;

    public Board(BoardRequestDto boardRequestDto, String imgPath) {
        this.title = boardRequestDto.getTitle();
        this.image = imgPath;
        this.star = boardRequestDto.getStar();
        this.location = boardRequestDto.getLocation();
        this.placename = boardRequestDto.getPlacename();
        this.content = boardRequestDto.getContent();
        this.season = boardRequestDto.getSeason();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
