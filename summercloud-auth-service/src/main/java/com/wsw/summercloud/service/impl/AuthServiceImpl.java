package com.wsw.summercloud.service.impl;

import com.wsw.summercloud.domain.User;
import com.wsw.summercloud.mapper.AuthMapper;
import com.wsw.summercloud.service.AuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author WangSongWen
 * @Date: Created in 17:45 2020/11/12
 * @Description:
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Resource
    private AuthMapper authMapper;

    @Override
    public User auth(String username, String password) {
        User user = authMapper.getUserByUAP(username, password);
        if (null == user){
            throw new SecurityException("用户名或密码错误!");
        }
        return user;
    }
}
