package com.wsw.fusertaskmanager.mapper;

import com.wsw.fusertaskmanager.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author WangSongWen
 * @Date: Created in 17:39 2020/11/12
 * @Description:
 */
@Mapper
public interface AuthMapper {
    User getUserByUAP(@Param("username") String username, @Param("password") String password);
}
