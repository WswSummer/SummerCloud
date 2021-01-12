package com.wsw.summercloud.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.wsw.summercloud.domain.User;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * @Author WangSongWen
 * @Date: Created in 16:51 2021/1/12
 * @Description: Jwt工具类
 */
public class JwtUtil {
    /**
     * @description: 生成token
     * @author: wangsongwen
     * @date: 2020/12/2 16:14
     **/
    private String getToken(User user, String tokenKey, Integer authTokenET) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        user.setPassword(null);  // jwt的json数据不能包含敏感信息
        String authToken = null;

        try {
            String userString = objectMapper.writeValueAsString(user);
            // 对密匙进行Base64编码
            String base64 = new BASE64Encoder().encode(tokenKey.getBytes());
            // 生成密匙对象,会根据base64长度自动选择相应的HMAC算法
            SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
            // 利用jwt生成token
            authToken = Jwts.builder()
                    .setSubject(userString)
                    .setExpiration(new Date(System.currentTimeMillis() + authTokenET))  // token过期时间
                    .signWith(secretKey)
                    .compact();

            return authToken;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @description: 刷新token
     * @author: wangsongwen
     * @date: 2020/12/2 16:15
     **/
    private String refreshToken(User user, String tokenKey, Integer refreshTokenET) {
        String refreshToken = null;
        try {
            refreshToken = getToken(user, tokenKey, refreshTokenET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refreshToken;
    }
}
