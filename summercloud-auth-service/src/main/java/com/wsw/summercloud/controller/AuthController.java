package com.wsw.summercloud.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.wsw.summercloud.api.CommonResult;
import com.wsw.summercloud.config.AuthConfig;
import com.wsw.summercloud.domain.User;
import com.wsw.summercloud.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author WangSongWen
 * @Date: Created in 17:47 2020/11/12
 * @Description: 认证中心颁发给前端token与refresh_token
 */
@RestController
public class AuthController {
    @Resource
    private AuthService authService;

    @Resource
    private AuthConfig authConfig;

    @Value("${token.expire.time}")
    private long tokenExpireTime;

    @Value("${refresh.token.expire.time}")
    private long refreshTokenExpireTime;

    @Value("${jwt.refresh.token.key.format}")
    private String jwtRefreshTokenKeyFormat;

    @Value("${jwt.blacklist.key.format}")
    private String jwtBlacklistKeyFormat;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/auth")
    @ResponseBody
    public CommonResult<Map> auth(@RequestParam("username") String username, @RequestParam("password") String password) {
        CommonResult<Map> commonResult;
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            User user = authService.auth(username, password);
            // 生成token
            String token = buildJWT(user.getUsername());
            // 生成refreshToken
            String refreshToken = UUID.randomUUID().toString().replaceAll("-","");
            // 保存refreshToken至redis，使用hash结构保存使用中的token以及用户标识
            String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
            stringRedisTemplate.opsForHash().put(refreshTokenKey, "token", token);
            stringRedisTemplate.opsForHash().put(refreshTokenKey, "username", user.getUsername());
            // refreshToken设置过期时间
            stringRedisTemplate.expire(refreshTokenKey, refreshTokenExpireTime, TimeUnit.MILLISECONDS);
            // 返回结果
            map.put("user", user);
            map.put("token", token);
            map.put("refreshToken", refreshToken);
            commonResult = CommonResult.success(map);
        } catch (Exception e) {
            commonResult = CommonResult.failed();
        }
        return commonResult;
    }

    /**
     * @description: 刷新JWT
     * @author: wangsongwen
     * @date: 2021/1/14 10:38
     **/
    @GetMapping("/token/refresh")
    public CommonResult<Map> refreshToken(@RequestParam String refreshToken){
        CommonResult<Map> commonResult;
        Map<String,Object> resultMap = new HashMap<>();
        String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
        String userName = (String)stringRedisTemplate.opsForHash().get(refreshTokenKey, "userName");
        if(StringUtils.isBlank(userName)){
            resultMap.put("code", "10001");
            resultMap.put("msg", "refreshToken过期");
            commonResult = CommonResult.unauthorized(resultMap);
            return commonResult;
        }
        String newToken = buildJWT(userName);
        // 替换当前token，并将旧token添加到黑名单
        String oldToken = (String)stringRedisTemplate.opsForHash().get(refreshTokenKey, "token");
        stringRedisTemplate.opsForHash().put(refreshTokenKey, "token", newToken);
        stringRedisTemplate.opsForValue().set(String.format(jwtBlacklistKeyFormat, oldToken), "",
                tokenExpireTime, TimeUnit.MILLISECONDS);
        resultMap.put("code", "10000");
        resultMap.put("data", newToken);
        commonResult = CommonResult.success(resultMap);
        return commonResult;
    }

    private String buildJWT(String userName){
        String secretKey = authConfig.getSecretKey();  // token密钥
        //生成jwt
        Date now = new Date();
        Algorithm algo = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer("WSW")
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + tokenExpireTime))
                .withClaim("userName", userName)//保存身份标识
                .sign(algo);
        return token;
    }
}
