package com.example.miniproject.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardRequestDto {
    private String title;

    private MultipartFile image;

    private int star;

    private String location;

    private String placename;

    private String content;

    private String season;
}
