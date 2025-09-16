package net.juststock.trading.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;

@Component
public class JwtUtil {

    private final Key key;
    private final long EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24h for filter validation window

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        Long uid = null;
        Object val = body.get("uid");
        if (val instanceof Number n) {
            uid = n.longValue();
        } else if (val instanceof String s) {
            try { uid = Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }
        if (uid != null) return uid;
        // Fallback for older tokens
        Object legacy = body.get("userId");
        if (legacy instanceof Number n2) return n2.longValue();
        if (legacy instanceof String s2) {
            try { return Long.parseLong(s2); } catch (NumberFormatException ignored) {}
        }
        return null;
    }
}
