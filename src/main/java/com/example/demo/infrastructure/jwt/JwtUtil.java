package com.example.demo.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    //private final SecretKey key = Jwts.SIG.HS256.key().build();

    @Value("${security.jwt.secret-key}")
    private final String key;

    public JwtUtil(@Value("${security.jwt.secret-key}") String key) {
        this.key = key;
    }


    public String createToken(long userPk, long validTime) {
        Date now = new Date();

        String subject = String.valueOf(userPk);

        String token = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

//    public String createToken(long userPk, long validTime, Map<String, Object> claims) {
//        Date now = new Date();
//
//        String subject = String.valueOf(userPk);
//
//        String token = Jwts.builder()
//                .claims(claims)
//                .setSubject(subject)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + validTime))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//
//        return token;
//    }


    // 토큰에서 유저정보 얻기
    public long getUserPk(String token) {
        String userPk = getSpecifiedClaim(token, Claims.SUBJECT, String.class);
        return Long.valueOf(userPk);
    }

    public Date getExpiredDate(String token) {
        return getSpecifiedClaim(token, Claims.EXPIRATION, Date.class);
    }

    public <T> T getSpecifiedClaim(String token, String claimName, Class<T> requiredType) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName, requiredType);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                                .verifyWith(getSignKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));
    }

    public boolean validate(String token) {
        Claims payload = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
        return payload.getExpiration().after(new Date());
    }

    public static String createKey() {
        return Base64.getEncoder().encodeToString(Jwts.SIG.HS256.key().build().getEncoded());
    }

}
