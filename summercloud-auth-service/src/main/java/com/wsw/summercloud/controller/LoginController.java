package com.wsw.summercloud.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsw.summercloud.api.CommonResult;
import com.wsw.summercloud.config.AuthConfig;
import com.wsw.summercloud.domain.User;
import com.wsw.summercloud.service.AuthService;
import com.wsw.summercloud.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author WangSongWen
 * @Date: Created in 17:47 2020/11/12
 * @Description: 认证中心颁发给前端的token分为auth_token(一般token - 过期时间短)与refresh_token(刷新token - 过期时间长)
 */
@RestController
public class AuthController {
    @Resource
    private AuthService authService;
    @Resource
    private AuthConfig authConfig;
    @Resource
    private JwtUtil jwtUtil;
    @Value("${jwt.authTokenET}")
    //@Value("#{T(java.lang.Integer).parseInt('${jwt.authTokenET}')}")
    private Integer authTokenET;  // 认证token 过期时间
    @Value("${jwt.refreshTokenET}")
    //@Value("#{T(java.lang.Integer).parseInt('${jwt.refreshTokenET}')}")
    private Integer refreshTokenET;  // 刷新token 过期时间

    @PostMapping("/auth")
    @ResponseBody
    public CommonResult<Map> auth(@RequestParam("username") String username, @RequestParam("password") String password) {
        String tokenKey = authConfig.getSecretKey();  // token密钥
        CommonResult<Map> commonResult = null;
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            User user = authService.auth(username, password);
            map.put("user", user);
            String auth_token = jwtUtil.getToken(user, tokenKey, authTokenET);
            String refresh_token = jwtUtil.refreshToken(user, tokenKey, refreshTokenET);
            map.put("auth_token", auth_token);
            map.put("refresh_token", refresh_token);
            commonResult = CommonResult.success(map);
        } catch (Exception e) {
            commonResult = CommonResult.failed();
        }

        return commonResult;
    }
}
