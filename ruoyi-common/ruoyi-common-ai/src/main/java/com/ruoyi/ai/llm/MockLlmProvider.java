package com.ruoyi.ai.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Mock LLM Provider - 无需API Key即可运行
 */
@Component
public class MockLlmProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(MockLlmProvider.class);

    private static final String MOCK_RESPONSE = "你好！我是 CoolAI 助手（模拟模式）。\n\n" +
            "当前未接入真实 LLM API，这是模拟回复。\n" +
            "要启用真实 AI 对话，请在 Nacos 配置中设置 OPENAI_API_KEY 或 ANTHROPIC_API_KEY。\n\n" +
            "你可以通过以下方式体验项目功能：\n" +
            "1. 注册/登录 - 完整可用\n" +
            "2. 创建会话/发消息 - 使用模拟回复\n" +
            "3. TTS语音合成 - Edge-TTS免费可用\n" +
            "4. 知识库CRUD - 完整可用\n" +
            "5. 视频生成/人物替换 - Mock引擎返回示例";

    @Override
    public String getName() { return "mock"; }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public LlmResponse chat(LlmRequest request) {
        log.info("MockLLM: 模拟对话响应");
        String userMessage = extractLastUserMessage(request.getMessages());
        String response = generateSmartResponse(userMessage);
        Map<String, Object> usage = new HashMap<>();
        usage.put("promptTokens", estimateTokens(userMessage));
        usage.put("completionTokens", estimateTokens(response));
        usage.put("mockProvider", true);
        return new LlmResponse(response, "mock-llm", usage, "stop");
    }

    @Override
    public void chatStream(LlmRequest request, StreamCallback callback) {
        log.info("MockLLM: 模拟流式对话响应");
        String response = generateSmartResponse(extractLastUserMessage(request.getMessages()));
        new Thread(() -> {
            try {
                for (char c : response.toCharArray()) {
                    callback.onToken(String.valueOf(c));
                    Thread.sleep(20);
                }
                Map<String, Object> usage = new HashMap<>();
                usage.put("promptTokens", estimateTokens(response));
                usage.put("completionTokens", estimateTokens(response));
                usage.put("mockProvider", true);
                callback.onDone(new LlmResponse(response, "mock-llm", usage, "stop"));
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    @Override
    public List<float[]> embeddings(List<String> texts) {
        Random random = new Random(42);
        List<float[]> result = new ArrayList<>();
        for (String text : texts) {
            random.setSeed(text.hashCode());
            float[] vector = new float[384];
            float sum = 0;
            for (int i = 0; i < 384; i++) {
                vector[i] = (float) random.nextGaussian();
                sum += vector[i] * vector[i];
            }
            float norm = (float) Math.sqrt(sum);
            for (int i = 0; i < 384; i++) {
                vector[i] /= norm;
            }
            result.add(vector);
        }
        return result;
    }

    @Override
    public List<String> listModels() { return Arrays.asList("mock-llm", "mock-llm-free"); }

    private String extractLastUserMessage(List<Map<String, String>> messages) {
        if (messages == null || messages.isEmpty()) return "";
        for (int i = messages.size() - 1; i >= 0; i--) {
            if ("user".equals(messages.get(i).get("role"))) {
                return messages.get(i).get("content");
            }
        }
        return messages.get(messages.size() - 1).get("content");
    }

    private String generateSmartResponse(String userMessage) {
        if (userMessage == null || userMessage.isEmpty()) return MOCK_RESPONSE;
        String lower = userMessage.toLowerCase();
        if (lower.contains("你好") || lower.contains("hello")) return "你好！我是 CoolAI 模拟助手。当前为模拟模式，接入 LLM API 后将提供真实 AI 对话。";
        if (lower.contains("tts") || lower.contains("语音")) return "TTS 语音合成功能已就绪！你可以通过 TTS 模块体验 Edge-TTS 免费语音合成，无需 API Key。";
        if (lower.contains("知识库") || lower.contains("rag")) return "RAG 知识库功能已就绪。你可以创建知识库、上传文档、进行知识检索。";
        if (lower.contains("帮助") || lower.contains("help")) return MOCK_RESPONSE;
        return MOCK_RESPONSE + "\n\n你的问题是：「" + userMessage + "」\n接入 LLM 后将获得智能回复。";
    }

    private int estimateTokens(String text) { return text == null ? 0 : text.length() / 4; }
}
