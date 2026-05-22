package com.ruoyi.ai.llm;

import java.util.List;
import java.util.Map;

/**
 * LLM Provider接口 - 策略模式，支持多种LLM后端
 */
public interface LlmProvider {

    /** Provider名称 */
    String getName();

    /** 同步对话 */
    LlmResponse chat(LlmRequest request);

    /** 流式对话 */
    void chatStream(LlmRequest request, StreamCallback callback);

    /** 文本嵌入 */
    List<float[]> embeddings(List<String> texts);

    /** 是否可用 */
    boolean isAvailable();

    /** 可用模型列表 */
    List<String> listModels();

    /** LLM请求 */
    class LlmRequest {
        private String model;
        private List<Map<String, String>> messages;
        private float temperature = 0.7f;
        private int maxTokens = 4096;
        private List<Map<String, Object>> tools;

        public LlmRequest() {}

        public LlmRequest(String model, List<Map<String, String>> messages) {
            this.model = model;
            this.messages = messages;
        }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<Map<String, String>> getMessages() { return messages; }
        public void setMessages(List<Map<String, String>> messages) { this.messages = messages; }
        public float getTemperature() { return temperature; }
        public void setTemperature(float temperature) { this.temperature = temperature; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
        public List<Map<String, Object>> getTools() { return tools; }
        public void setTools(List<Map<String, Object>> tools) { this.tools = tools; }
    }

    /** LLM响应 */
    class LlmResponse {
        private String content;
        private String model;
        private Map<String, Object> usage;
        private String finishReason;

        public LlmResponse() {}

        public LlmResponse(String content, String model, Map<String, Object> usage, String finishReason) {
            this.content = content;
            this.model = model;
            this.usage = usage;
            this.finishReason = finishReason;
        }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Map<String, Object> getUsage() { return usage; }
        public void setUsage(Map<String, Object> usage) { this.usage = usage; }
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }

    /** 流式回调 */
    interface StreamCallback {
        void onToken(String token);
        void onDone(LlmResponse response);
        void onError(Exception e);
    }
}
