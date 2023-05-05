package com.example.miniproject.config.jwt;

import com.example.miniproject.config.security.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey); //Base64로 인코딩되어 있는 것을, 값을 가져와서(getDecoder()) 디코드하고(decode(secretKey)), byte 배열로 반환
        key = Keys.hmacShaKeyFor(bytes); //반환된 bytes 를 hmacShaKeyFor() 메서드를 사용해서 Key 객체에 넣기
    }

    public String resolveToken(HttpServletRequest request) {
        //HttpServletRequest request 객체 안에 들어간 Token 값을 가져옴
        //() 안에는 어떤 KEY 를 가져올지 정할 수 있음(여기선 AUTHORIZATION_HEADER 안에 있는 KEY 의 값을 가져옴)
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            //substring() 메소드를 사용해서, 앞의 일곱 글자를 지워줌 --> 앞의 일곱 글자는 Token 과 관련 없는 String 값이므로
            return bearerToken.substring(7);
        }
        return null;
    }

    // JWT 생성
    public String createToken(String username) {
        Date date = new Date();

        return BEARER_PREFIX +  //Token 앞에 들어가는 부분임
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            //Jwts: 위에서 JWT 생성 시 사용했던 것
            //parserBuilder(): 검증 방법
            //setSigningKey(key): Token 을 만들 때 사용한 KEY
            //parseClaimsJws(token): 어떤 Token 을 검증할 것인지
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기 --> 위에서 validateToken() 으로 토큰을 검증했기에 이 코드를 사용할 수 있는 것
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }


}
