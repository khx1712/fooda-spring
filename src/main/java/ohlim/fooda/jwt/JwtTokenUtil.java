package ohlim.fooda.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtTokenUtil{

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public static final long JWT_ACCESS_TOKEN_VALIDITY = 24 * 60 * 60 * 7; //일주일
    public static final long JWT_REFRESH_TOKEN_VALIDITY = 24 * 60 * 60 * 30; //30일

    // 비밀키에 서명을 한다.
    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // jwt token의 payload에서 Claims을 추출합니다.
    public Claims extractAllClaims(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
        return claims;
    }

    // jwt token에서 userName을 가져옵니다.
    public String getUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    // jwt token이 만료되었는지 확인합니다.
    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // jwt token에서 권한을 가져옵니다.
    public List<String> getRoles(String token) {
        return extractAllClaims(token).get("role", List.class);
    }

    // accessToken을 생성합니다.
    public String generateAccessToken(UserDetails userDetails) {
        Claims claims = Jwts.claims();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority: userDetails.getAuthorities()) {
            roles.add(grantedAuthority.getAuthority());
        }
        claims.put("role",roles);
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256).compact();
    }

    // refreshToken을 생성합니다.
    public String generateRefreshToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256).compact();
    }

    // token이 유효한지(token의 claims의 userName과 사용자 아이디가 일치하는지, token이 만료되었는지) 확인합니다.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}