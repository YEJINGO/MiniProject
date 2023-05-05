package com.example.miniproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MsgAndHttpStatusDto {

    private String message;
    private int statusCode;

}
