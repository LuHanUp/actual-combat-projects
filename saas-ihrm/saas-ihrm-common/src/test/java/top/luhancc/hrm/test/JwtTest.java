package top.luhancc.hrm.test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.junit.Test;

import java.util.Date;

/**
 * @author luHan
 * @create 2021/5/14 16:50
 * @since 1.0.0
 */
public class JwtTest {

    @Test
    public void testCreateJwtToken() {
        String token = Jwts.builder()
                .setId("8888888")
                .setSubject("aaaa")
                .setIssuedAt(new Date())
                // claim可以自定义内容,可以使用多次
                .claim("name", "123")
                .claim("code", "1111")
                .signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode("ihrm"))
                .compact();
        System.out.println(token);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI4ODg4ODg4Iiwic3ViIjoiYWFhYSIsImlhdCI6MTYyMDk4MzEyMywibmFtZSI6IjEyMyIsImNvZGUiOiIxMTExIn0.WV3C68HeP6s0mX0m72kvErazycNfKi5hHE6Jt6IRFQUkLGxicoG4cOTICSk7_0JnNIcyB8cCuZlmQev0EL2Oog";
        Claims claims = Jwts.parser().setSigningKey(TextCodec.BASE64.encode("ihrm")).parseClaimsJws(token).getBody();
        System.out.println(claims.getId());
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuedAt());
        System.out.println(claims.get("name"));
        System.out.println(claims.get("code"));
    }
}
