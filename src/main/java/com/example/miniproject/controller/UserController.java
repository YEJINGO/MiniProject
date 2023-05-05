package com.example.miniproject.controller;

import com.example.miniproject.dto.LoginRequestDto;
import com.example.miniproject.dto.MsgAndHttpStatusDto;
import com.example.miniproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/login")
    public ResponseEntity<MsgAndHttpStatusDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        userService.login(loginRequestDto,response);
        return ResponseEntity.ok(new MsgAndHttpStatusDto("로그인 성공", HttpStatus.OK.value()));
    }
}
