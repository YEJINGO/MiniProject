package com.example.miniproject.config.jwt;

import com.example.miniproject.dto.MsgAndHttpStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.resolveAccessToken(request);
        String refreshToken = jwtUtil.resolveRefreshToken(request);


        if (accessToken != null) {
            String userId = jwtUtil.getUserInfoFromToken(accessToken).getSubject();

            if (jwtUtil.validateToken(accessToken, jwtUtil.getAccessKey())) {
                this.setAuthentication(accessToken);
            } else if (!jwtUtil.validateToken(accessToken, jwtUtil.getAccessKey()) && refreshToken != null) {
                boolean validateRefreshToken = jwtUtil.validateToken(refreshToken, jwtUtil.getRefreshKey());

                boolean isRefreshToken = jwtUtil.existsRefreshToken(refreshToken);
                if (validateRefreshToken && isRefreshToken) {
                    /// 토큰 발급
                    String newAccessToken = jwtUtil.createAccessToken(userId);
                    /// 헤더에 어세스 토큰 추가
                    jwtUtil.setHeaderAccessToken(response, newAccessToken);
                    /// 컨텍스트에 넣기
                    this.setAuthentication(newAccessToken);
                }
            }

        }
        filterChain.doFilter(request, response);

    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new MsgAndHttpStatusDto(msg, statusCode));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

