package com.example.dentalscheduler.security.jwt;

import com.example.dentalscheduler.exceptions.CustomException;
import com.example.dentalscheduler.security.CustomUserDetails;
import com.example.dentalscheduler.security.jwt.dto.TokenDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS512.getValue());
        return Jwts.parser().setSigningKey(secretKeySpec).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {

        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(CustomUserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());

        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        claims.put("role", authoritiesString);
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public TokenDetails getTokenDetails(String token) {
        Date expirationDateFromToken = getExpirationDateFromToken(token);
        String usernameFromToken = getUsernameFromToken(token);
        Date issuedAtDateFromToken = getIssuedAtDateFromToken(token);
        return TokenDetails.builder()
                .expirationDate(expirationDateFromToken)
                .issuedAt(issuedAtDateFromToken)
                .token(token)
                .username(usernameFromToken).build();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS512.getValue());
        try {
            Jwts.parser()
                    .setSigningKey(secretKeySpec)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new CustomException("Invalid Jwt token", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
        }
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
