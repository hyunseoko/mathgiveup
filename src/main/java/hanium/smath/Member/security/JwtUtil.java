package hanium.smath.Member.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private static final long EXPIRATION_TIME = 86400000; // 일반 로그인 만료기간 : 1일
    private static final long EXTENDED_EXPIRATION_TIME = 604800000; // 자동 로그인 만료기간 : 7일

    public String generateToken(String loginId) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);
        System.out.println("Generating token. Current Time: " + now + ", Expiration Time: " + expiration);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now) // 발급 시간 설정
//                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String generateTokenWithExtendedExpiry(String loginId) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + EXTENDED_EXPIRATION_TIME);
        System.out.println("Generating token with extended expiry. Current Time: " + now + ", Expiration Time: " + expiration);

        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(now) // 발급 시간 설정
//                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 자동 로그인 test을 위한.
    public String extractLoginId(String token) {
        String loginId = Jwts.parser()
//        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject();
        System.out.println("Extracted Login ID from token: " + loginId);
        return loginId;
    }

    // token 유효성 확인을 위한
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractLoginId(token);
        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        System.out.println("Validating token for username: " + username + ". Token is valid: " + isValid);
        return isValid;
//        System.out.println("Validating token for username: " + username);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration();
        boolean isExpired = expiration.before(new Date());
        System.out.println("Token expiration time: " + expiration + ". Is token expired: " + isExpired);
        return isExpired;
//        final Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration();
//        return expiration.before(new Date());
    }

    // 만료 시간을 출력하는 메서드 추가
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        Date expiration = claims.getExpiration();
        System.out.println("Extracted expiration date from token: " + expiration);
        return expiration;
//        return claims.getExpiration();
    }
}
