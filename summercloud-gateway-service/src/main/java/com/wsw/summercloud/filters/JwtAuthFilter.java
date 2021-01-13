package com.wsw.summercloud.filters;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsw.summercloud.api.CommonResult;
import com.wsw.summercloud.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.misc.BASE64Encoder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * @Author WangSongWen
 * @Date: Created in 17:32 2021/1/12
 * @Description:
 */
@Component
@Slf4j
@ConfigurationProperties("my.jwt")
public class JwtAuthFilter implements GlobalFilter, Ordered {
    private String[] skipAuthUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
        if (null != skipAuthUrls && isSkipUrl(url)) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //从请求头中取得token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            CommonResult res = new CommonResult(401, "401 unauthorized");
            byte[] responseByte = JSONObject.toJSON(res).toString().getBytes(StandardCharsets.UTF_8);

            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        }

        //请求中的token是否有效
        // token存在，验证有效性
        //String tokenKey = authConfig.getSecretKey();
        String tokenKey = "1234567890-1234567890-1234567890";
        String base64 = new BASE64Encoder().encode(tokenKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(base64.getBytes());
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            String userJson = claimsJws.getBody().getSubject();
            log.info(userJson);
            User user = objectMapper.readValue(userJson, User.class);
            request.getHeaders().set("$user", user.toString());
        } catch (JsonProcessingException e){
            // json转换失败抛出异常
            e.printStackTrace();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            CommonResult res = new CommonResult(500, "json convert error");
            byte[] responseByte = JSONObject.toJSON(res).toString().getBytes(StandardCharsets.UTF_8);

            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        } catch (JwtException e) {
            // token过期抛出异常
            e.printStackTrace();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            CommonResult res = new CommonResult(404, "token expired");
            byte[] responseByte = JSONObject.toJSON(res).toString().getBytes(StandardCharsets.UTF_8);

            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        }

        //如果各种判断都通过，执行chain上的其他业务逻辑
        return chain.filter(exchange);
    }

    /**
     * 判断当前访问的url是否开头URI是在配置的忽略url列表中
     *
     * @param url
     * @return
     */
    public boolean isSkipUrl(String url) {
        for (String skipAuthUrl : skipAuthUrls) {
            if (url.startsWith(skipAuthUrl)) {
                return true;
            }
        }
        return false;
    }

    // 优先级
    @Override
    public int getOrder() {
        return -1;
    }
}
