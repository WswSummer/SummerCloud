package com.wsw.fusertaskmanager.service;

import com.wsw.fusertaskmanager.domain.User;

/**
 * @Author WangSongWen
 * @Date: Created in 17:45 2020/11/12
 * @Description:
 */
public interface AuthService {
    User auth(String username, String password);
}
