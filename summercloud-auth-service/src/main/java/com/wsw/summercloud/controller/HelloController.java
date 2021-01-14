package com.wsw.summercloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author WangSongWen
 * @Date: Created in 11:13 2021/1/14
 * @Description:
 */
@RestController
public class HelloController {
    @GetMapping("/auth/hello")
    public String hello(){
        return "hello";
    }
}
