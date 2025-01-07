package org.balllog.backend.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.balllog.backend.auth.userdetails.BallLogUserDetails;
import org.balllog.backend.global.exception.GeneralException;
import org.balllog.backend.global.response.Code;
import org.balllog.backend.jwt.dto.TokenDto;
import org.balllog.backend.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String SOCIAL_ID_KEY = "socialId";
    private static final String SOCIAL_TYPE_KEY = "socialType";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(BallLogUserDetails userDetails) {
        long now = System.currentTimeMillis();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        // AccessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(SOCIAL_ID_KEY, userDetails.getSocialId())
                .claim(SOCIAL_TYPE_KEY, userDetails.getSocialType())
                .claim(AUTHORITIES_KEY, getAuthorities(userDetails))
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshTokenExpiresIn(refreshTokenExpiresIn.getTime())
                .build();
    }

    private String getAuthorities(BallLogUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new GeneralException(Code.UNAUTHORIZED);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        User user = User.builder()
                .id(Long.parseLong(claims.getSubject()))
                .socialId(claims.get(SOCIAL_ID_KEY).toString())
                .socialType(User.SocialType.fromString(claims.get(SOCIAL_TYPE_KEY).toString()))
                .build();

        BallLogUserDetails principal = new BallLogUserDetails(user);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SignatureException e) {
            throw new GeneralException(Code.INVALID_JWT_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new GeneralException(Code.MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(Code.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new GeneralException(Code.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new GeneralException(Code.ILLEGAL_JWT);
        }

    }

}
