package com.fisher.newaccountpoc.common.util;

import com.fisher.newaccountpoc.dto.TokenUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            "2sz1hxcULStC7Ml9/ZHP3fFJ8P0p4MDrtQeo8JFzwtk=".repeat(4).getBytes()
    );
    private static final long EXPIRATION_MS = 3600000; // 1小时

    // Token 生成
    public String generateToken(TokenUserDTO user) {
        return Jwts.builder()
            .subject(user.getUserName())
            .claim("roles", user.getRoles())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    // Token 解析
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Token 验证
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
