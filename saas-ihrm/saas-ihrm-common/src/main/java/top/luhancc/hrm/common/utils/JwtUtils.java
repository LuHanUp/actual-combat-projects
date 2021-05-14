package top.luhancc.hrm.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.Map;

/**
 * @author luHan
 * @create 2021/5/14 17:07
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "jwt.config")
public class JwtUtils {
    // 私钥key
    private String key;
    // 签名的有效时间，单位s
    private long ttl;

    public String createJwt(String userId, String username, Map<String, Object> map) {
        // 设置失效时间:当前时间
        long now = System.currentTimeMillis();
        long exp = now + (ttl * 1000);
        JwtBuilder jwtBuilder = Jwts.builder();
        if (map != null) {
            jwtBuilder.setClaims(map);
        }
        String token = jwtBuilder
                .setId(userId)
                .setSubject(username)
                .setIssuedAt(new Date())
                // claim可以自定义内容,可以使用多次
                .setClaims(map)
                .setExpiration(new Date(exp))
                .signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode(key))
                .compact();
        return token;
    }

    public Claims parseJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.encode(key))
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}
