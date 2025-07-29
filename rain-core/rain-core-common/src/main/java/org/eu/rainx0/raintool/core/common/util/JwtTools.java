package org.eu.rainx0.raintool.core.common.util;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import org.eu.rainx0.raintool.core.common.exception.SystemException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 18:46
 */
public class JwtTools {
    private static final String SECRET = "hah123sadfsdfsdfsdfsdfsdfsdfsdfsckls";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes()); // with HMAC-SHA algorithms


    // token 有效期（单位：毫秒）如 1 小时
    private static final long ACCESS_TOKEN_TTL_MS = Duration.ofHours(1).toMillis();
    // refresh token 有效期（单位：毫秒）如 7 天
    private static final long REFRESH_TOKEN_TTL_MS = Duration.ofDays(7).toMillis();

    public static String[] generateAccessToken(String username) {
        String sessionId = RandomTools.uuid32();

        Map<String, Object> accessClaims = new HashMap<>(2);
        accessClaims.put("type", "access");
        accessClaims.put("sessionId", sessionId);
        String accessToken = generateToken(username, accessClaims, ACCESS_TOKEN_TTL_MS);

        Map<String, Object> refreshClaims = new HashMap<>(2);
        refreshClaims.put("type", "refresh");
        refreshClaims.put("sessionId", sessionId);
        String refreshToken = generateToken(username, refreshClaims, REFRESH_TOKEN_TTL_MS);

        return new String[]{accessToken, refreshToken};
    }

    /**
     * 生成 Token
     */
    public static String generateToken(String subject, Map<String, Object> claims, long ttlMs) {
        return Jwts.builder()
            .claims(claims)                    // 自定义 payload
            .subject(subject)                 // 通常是 userId 或用户名
            .issuedAt(new Date())             // 签发时间
            .expiration(new Date(System.currentTimeMillis() + ttlMs)) // 过期时间
            .signWith(KEY, Jwts.SIG.HS256)
            .compact();
    }


    /**
     * 解析 Token，获取 claims
     */
    public static Claims parseToken(String token) throws Exception {
        JwtParser parser = Jwts.parser()
            .verifyWith(KEY)
            .build();

        Claims claims = parser
            .parseSignedClaims(token)
            .getPayload();

        boolean expired = claims.getExpiration().before(new Date());
        if (expired) {
            throw new SystemException("Token expired");
        }

        return claims;
    }

    /**
     * 获取 subject（比如 userId）
     */
    public static String getSubject(String token) throws Exception {
        return parseToken(token).getSubject();
    }

    /**
     * 获取某个 claim
     */
    public static Object getClaim(String token, String key) throws Exception {
        return parseToken(token).get(key);
    }
}
