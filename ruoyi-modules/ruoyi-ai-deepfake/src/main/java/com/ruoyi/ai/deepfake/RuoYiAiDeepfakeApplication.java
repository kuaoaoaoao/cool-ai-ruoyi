package com.ruoyi.ai.deepfake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiAiDeepfakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAiDeepfakeApplication.class, args);
    }
}
