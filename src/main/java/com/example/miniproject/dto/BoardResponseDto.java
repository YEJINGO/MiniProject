package com.example.miniproject.dto;

import com.example.miniproject.entity.Board;
import lombok.Data;

@Data
public class BoardResponseDto {
    private Long id;

    private String title;

    private String image;

    private int star;

    private String location;

    private String placename;

    private String content;

    private String season;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.image = board.getImage();
        this.star = board.getStar();
        this.location = board.getLocation();
        this.placename = board.getPlacename();
        this.content = board.getContent();
        this.season = board.getSeason();
    }
}
