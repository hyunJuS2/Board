package com.sparta.board.jwt;


import com.sparta.board.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component // Bean 등록
public class JwtUtil {
//    0. 데이터 생성
//    1. JWT 생성 메서드
//    2. 생성된 JWT를 Response 객체 Header에 바로 넣어버리기!
//    3. Header에 들어있는 JWT 토큰을 Substring
//    4. JWT 검증
//    5. JWT에서 사용자 정보 가져오기


    //0. JWT 데이터

    //Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    //사용자 권한 값의 KEY 값
    public static final String AUTHORIZATION_KEY = "auth";
    //Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    //토큰 만료시간
    public static final long TOKEN_TIME = 60 * 60 * 1000L; //60분

    //application.properties 안의 값을 가져오는 방법
    @Value("${jwt.secret.key}") //Base63 Encode한 SecretKey
    private String secretKey;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    //key field에 SecretKey값을 decode하여 담는다.
    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }


    //1. JWT 토큰 생성

    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자의 식별값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자의 권한정보 -> key - value 형태
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 현재 시간 + Token 만료 시간 = 최종 만료시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // secretkey 값, 암호화 알고리즘(HS256)
                        .compact();

    }

    // 2. 생성된 JWT를 Response 객체 Header에 바로 넣어버리기!

    public void addJwtToHeader(String token, HttpServletResponse response){
        response.addHeader(AUTHORIZATION_HEADER, token);
    }

    // 3. Header에 들어있는 JWT 토큰을 Substring

    public String substringToken(String tokenValue){
        if(StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)){
            //공백, null을 확인 할 수 있음 && BEARER_PREFIX로 시작하는지 알 수 있음.
            return tokenValue.substring(7);
        }
        logger.error("NOT FOUND TOKEN");
        throw new NullPointerException("NOT FOUND TOKEN");
    }

    //    4. JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // key로 token 검증
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    //    5. JWT에서 사용자 정보 가져오기

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody(); //body부분의 claims를 가지고 올 수 잇음
    }
}
