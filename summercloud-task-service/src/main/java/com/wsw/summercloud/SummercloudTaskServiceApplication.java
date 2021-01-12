package com.wsw.summercloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
public class SummercloudTaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummercloudTaskServiceApplication.class, args);
    }

}
