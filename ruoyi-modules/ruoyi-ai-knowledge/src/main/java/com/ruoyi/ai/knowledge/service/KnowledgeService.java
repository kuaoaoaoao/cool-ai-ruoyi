package com.ruoyi.ai.knowledge.service;

import com.ruoyi.ai.llm.LlmGatewayService;
import com.ruoyi.ai.llm.LlmProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KnowledgeService {
    @Autowired
    private LlmGatewayService llmGatewayService;

    public Map<String, Object> createBase(Map<String, Object> params) {
        Map<String, Object> base = new HashMap<>(params);
        base.put("id", UUID.randomUUID().toString());
        base.put("status", "active");
        return base;
    }

    public List<Map<String, Object>> listBases() { return new ArrayList<>(); }

    public Map<String, Object> uploadDocument(String baseId, Map<String, Object> params) {
        Map<String, Object> doc = new HashMap<>(params);
        doc.put("id", UUID.randomUUID().toString());
        doc.put("baseId", baseId);
        doc.put("status", "processing");
        return doc;
    }

    public Map<String, Object> query(String baseId, Map<String, Object> params) {
        String question = (String) params.get("question");
        String model = (String) params.getOrDefault("model", "mock-llm");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", "基于知识库内容回答问题。");
        messages.add(sysMsg);
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", question);
        messages.add(userMsg);
        LlmProvider.LlmResponse response = llmGatewayService.chat(new LlmProvider.LlmRequest(model, messages));
        Map<String, Object> result = new HashMap<>();
        result.put("answer", response.getContent());
        result.put("baseId", baseId);
        return result;
    }
}
