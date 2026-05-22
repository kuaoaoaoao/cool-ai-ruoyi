package com.ruoyi.ai.singing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiAiSingingApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAiSingingApplication.class, args);
    }
}
