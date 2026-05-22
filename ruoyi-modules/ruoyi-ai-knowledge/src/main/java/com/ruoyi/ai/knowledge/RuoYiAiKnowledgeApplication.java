package com.ruoyi.ai.knowledge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class RuoYiAiKnowledgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAiKnowledgeApplication.class, args);
    }
}
