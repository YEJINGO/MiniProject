package com.example.miniproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class FilterRequestDto {
    private String season;

    private String location;

    private String star;

    private String keyword;
}
