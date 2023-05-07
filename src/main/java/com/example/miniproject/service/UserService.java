package com.example.miniproject.service;

import com.example.miniproject.config.jwt.JwtUtil;
import com.example.miniproject.dto.LoginRequestDto;
import com.example.miniproject.entity.User;
import com.example.miniproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private final RedisService redisService;

    @Transactional
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String userId = loginRequestDto.getUserId();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("dd"));// 예외처리 해주기

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new IllegalStateException("dd");
//        }

        // 회원가입 기능(with passwordEncoder) 구현되면 이 아래 코드는 삭제하고 위의 주석을 해제하면 됩니다.
        if (!password.equals(user.getPassword())) {
            throw new IllegalArgumentException("dd");
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        redisService.setValues(refreshToken, user.getUserId());

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESHTOKEN_HEADER, refreshToken);
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        redisService.deleteValues(request.getHeader("RefreshToken"));
    }
}
