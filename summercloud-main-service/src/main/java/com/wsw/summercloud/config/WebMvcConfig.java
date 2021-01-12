package com.wsw.summercloud.config;

import com.wsw.summercloud.interceptor.JwtTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author WangSongWen
 * @Date: Created in 10:43 2020/11/13
 * @Description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    JwtTokenInterceptor jwtTokenInterceptor(){
        return new JwtTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor()).addPathPatterns("/**");
    }

}
