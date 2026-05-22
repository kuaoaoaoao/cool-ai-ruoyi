package com.ruoyi.ai.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiAiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAiAgentApplication.class, args);
    }
}
