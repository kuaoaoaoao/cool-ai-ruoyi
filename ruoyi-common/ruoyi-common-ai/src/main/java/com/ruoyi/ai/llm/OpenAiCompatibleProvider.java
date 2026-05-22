package com.ruoyi.ai.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI兼容API Provider (支持OpenAI/Azure/DeepSeek/Moonshot等)
 */
@Component
@ConditionalOnProperty(name = "ai.llm.openai.api-key", matchIfMissing = false)
public class OpenAiCompatibleProvider implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleProvider.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${ai.llm.openai.api-key:}")
    private String apiKey;

    @Value("${ai.llm.openai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${ai.llm.openai.models:gpt-3.5-turbo}")
    private String models;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

    @Override
    public String getName() { return "openai"; }

    @Override
    public boolean isAvailable() { return apiKey != null && !apiKey.isEmpty() && !apiKey.startsWith("fake"); }

    @Override
    public LlmResponse chat(LlmRequest request) {
        try {
            Map<String, Object> body = buildRequestBody(request);
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(mapper.writeValueAsString(body), MediaType.parse("application/json")))
                    .build();
            try (Response response = client.newCall(httpRequest).execute()) {
                String respBody = response.body() != null ? response.body().string() : "";
                JsonNode json = mapper.readTree(respBody);
                JsonNode choice = json.path("choices").path(0);
                String content = choice.path("message").path("content").asText("");
                JsonNode usage = json.path("usage");
                Map<String, Object> usageMap = new HashMap<>();
                usageMap.put("promptTokens", usage.path("prompt_tokens").asInt(0));
                usageMap.put("completionTokens", usage.path("completion_tokens").asInt(0));
                usageMap.put("totalTokens", usage.path("total_tokens").asInt(0));
                return new LlmResponse(content, json.path("model").asText(""), usageMap, choice.path("finish_reason").asText("stop"));
            }
        } catch (Exception e) {
            log.error("OpenAI chat error", e);
            throw new RuntimeException("OpenAI API调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void chatStream(LlmRequest request, StreamCallback callback) {
        try {
            Map<String, Object> body = buildRequestBody(request);
            body.put("stream", true);
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(mapper.writeValueAsString(body), MediaType.parse("application/json")))
                    .build();
            EventSource.Factory factory = EventSources.createFactory(client);
            factory.newEventSource(httpRequest, new EventSourceListener() {
                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    if ("[DONE]".equals(data)) {
                        callback.onDone(new LlmResponse("", request.getModel(), Collections.emptyMap(), "stop"));
                        return;
                    }
                    try {
                        JsonNode json = mapper.readTree(data);
                        String token = json.path("choices").path(0).path("delta").path("content").asText("");
                        if (!token.isEmpty()) callback.onToken(token);
                    } catch (Exception ignored) {}
                }
                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    callback.onError(t instanceof Exception ? (Exception) t : new Exception(t));
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public List<float[]> embeddings(List<String> texts) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "text-embedding-3-small");
            body.put("input", texts);
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + "/embeddings")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(mapper.writeValueAsString(body), MediaType.parse("application/json")))
                    .build();
            try (Response response = client.newCall(httpRequest).execute()) {
                String respBody = response.body() != null ? response.body().string() : "";
                JsonNode json = mapper.readTree(respBody);
                List<float[]> result = new ArrayList<>();
                for (JsonNode item : json.path("data")) {
                    List<Float> embedding = new ArrayList<>();
                    for (JsonNode v : item.path("embedding")) embedding.add((float) v.asDouble());
                    float[] arr = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) arr[i] = embedding.get(i);
                    result.add(arr);
                }
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("OpenAI embeddings调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> listModels() { return Arrays.asList(models.split(",")); }

    private Map<String, Object> buildRequestBody(LlmRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.getModel());
        body.put("messages", request.getMessages());
        body.put("temperature", request.getTemperature());
        body.put("max_tokens", request.getMaxTokens());
        if (request.getTools() != null && !request.getTools().isEmpty()) {
            body.put("tools", request.getTools());
        }
        return body;
    }
}
