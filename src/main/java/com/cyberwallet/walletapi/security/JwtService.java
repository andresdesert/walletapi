package com.cyberwallet.walletapi.security;

import com.cyberwallet.walletapi.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String jwtSecretHex;

    private Key secretKey;

    private static final long EXPIRATION_TIME_MS = 86400000; // 1 día

    @PostConstruct
    public void init() {
        try {
            this.secretKey = Keys.hmacShaKeyFor(Hex.decodeHex(jwtSecretHex.toCharArray()));
        } catch (DecoderException e) {
            log.error("❌ Error al decodificar la clave secreta JWT. Revisa tu .env o application-test.properties. Valor actual: {}", jwtSecretHex);
            throw new IllegalArgumentException("JWT_SECRET inválido. Debe ser una cadena hexadecimal válida.", e);
        }
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("⚠️ Token inválido detectado: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Método adicional para tests unitarios
    public void setSecretKeyHex(String hexSecret) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(Hex.decodeHex(hexSecret.toCharArray()));
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Clave secreta inválida en setSecretKeyHex()", e);
        }
    }
}
