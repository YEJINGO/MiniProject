package com.example.miniproject.controller;


import com.example.miniproject.config.security.UserDetailsImp;
import com.example.miniproject.dto.BoardRequestDto;
import com.example.miniproject.dto.BoardResponseDto;
import com.example.miniproject.service.BoardService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBoard(@ModelAttribute BoardRequestDto boardRequestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImp userDetails) throws IOException {
        return boardService.createBoard(boardRequestDto, userDetails);
    }
}
