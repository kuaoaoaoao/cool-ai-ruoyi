package com.ruoyi.ai.chat.service;

import com.ruoyi.ai.llm.LlmGatewayService;
import com.ruoyi.ai.llm.LlmProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI对话Service
 */
@Service
public class ChatService {

    @Autowired
    private LlmGatewayService llmGatewayService;

    /** 同步对话 */
    public Map<String, Object> chat(String message, String conversationId, String model) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", message);
        messages.add(msg);

        LlmProvider.LlmRequest request = new LlmProvider.LlmRequest(model, messages);
        LlmProvider.LlmResponse response = llmGatewayService.chat(request);

        Map<String, Object> result = new HashMap<>();
        result.put("content", response.getContent());
        result.put("model", response.getModel());
        result.put("conversationId", conversationId != null ? conversationId : UUID.randomUUID().toString());
        result.put("usage", response.getUsage());
        return result;
    }

    /** 流式对话 */
    public void chatStream(String message, String conversationId, String model, LlmProvider.StreamCallback callback) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> msg = new HashMap<>();
        msg.put("role", "user");
        msg.put("content", message);
        messages.add(msg);

        LlmProvider.LlmRequest request = new LlmProvider.LlmRequest(model, messages);
        llmGatewayService.chatStream(request, callback);
    }

    /** 可用模型列表 */
    public List<Map<String, Object>> listModels() {
        return llmGatewayService.listModels();
    }
}
