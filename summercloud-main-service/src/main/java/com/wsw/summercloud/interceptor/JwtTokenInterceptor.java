package com.wsw.summercloud.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsw.summercloud.annotation.JwtToken;
import com.wsw.summercloud.api.CommonResult;
import com.wsw.summercloud.config.AuthConfig;
import com.wsw.summercloud.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author WangSongWen
 * @Date: Created in 10:44 2020/11/13
 * @Description: 自定义JwtToken拦截器
 */
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {
    @Resource
    private AuthConfig authConfig;

    /**
     * 前端发送token分为auth_token(一般token-过期时间短)与refresh_token(刷新token-过期时间长)
     * auth_token若已过期,则用refresh_token进行验证, 具体怎么传, 前端控制, 后端只负责验证
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("JwtTokenInterceptor.preHandle");
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        response.setContentType("text/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取目标方法的Method对象
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(JwtToken.class)){
            // 方法包含JwtToken注解
            String token = request.getHeader("token");
            if (null == token){
                // token不存在
                JwtToken jwtToken = method.getAnnotation(JwtToken.class);
                if (jwtToken.required()){  // required属性为true
                    response.setStatus(401);
                    CommonResult<Object> commonResult = CommonResult.unauthorized();
                    String json = objectMapper.writeValueAsString(commonResult);
                    response.getWriter().println(json);
                    return false;
                }
            }else {
                // token存在，验证有效性
                String tokenKey = authConfig.getSecretKey();
                String base64 = new BASE64Encoder().encode(tokenKey.getBytes());
                SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
                try {
                    Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
                    String userJson = claimsJws.getBody().getSubject();
                    log.info(userJson);
                    User user = objectMapper.readValue(userJson, User.class);
                    request.setAttribute("$user", user);
                    return true;
                } catch (JsonProcessingException e){
                    // json转换失败抛出异常
                    e.printStackTrace();
                    response.setStatus(500);
                    CommonResult<Object> commonResult = CommonResult.failed("Json转换异常");
                    String json = objectMapper.writeValueAsString(commonResult);
                    response.getWriter().println(json);
                    return false;
                } catch (JwtException e) {
                    // token过期抛出异常
                    e.printStackTrace();
                    response.setStatus(404);
                    CommonResult<Object> commonResult = CommonResult.validateFailed();
                    String json = objectMapper.writeValueAsString(commonResult);
                    response.getWriter().println(json);
                    return false;
                }
            }
        }
        return true;
    }
}
