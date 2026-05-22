package com.ruoyi.ai.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM网关服务 - Provider注册、模型路由、自动降级
 */
@Service
public class LlmGatewayService {

    private static final Logger log = LoggerFactory.getLogger(LlmGatewayService.class);

    private final Map<String, LlmProvider> providers = new ConcurrentHashMap<>();
    private final Map<String, String> modelToProvider = new ConcurrentHashMap<>();

    @Autowired
    private List<LlmProvider> injectedProviders;

    @Autowired
    private MockLlmProvider mockLlmProvider;

    @PostConstruct
    public void init() {
        for (LlmProvider provider : injectedProviders) {
            if (!(provider instanceof MockLlmProvider)) {
                registerProvider(provider);
            }
        }
        registerProvider(mockLlmProvider);

        long realProviderCount = 0;
        for (LlmProvider p : injectedProviders) {
            if (!(p instanceof MockLlmProvider) && p.isAvailable()) realProviderCount++;
        }
        if (realProviderCount == 0) {
            log.warn("========== 未检测到真实 LLM Provider，已启用 Mock 模式 ==========");
            log.warn("   要启用真实 AI 对话，请在 Nacos 配置中设置 OPENAI_API_KEY");
        } else {
            log.info("已注册 {} 个真实 LLM Provider + 1 个 Mock 回退", realProviderCount);
        }
    }

    public void registerProvider(LlmProvider provider) {
        providers.put(provider.getName(), provider);
        for (String model : provider.listModels()) {
            modelToProvider.put(model, provider.getName());
        }
        log.info("Registered LLM provider: {} with models: {}", provider.getName(), provider.listModels());
    }

    public LlmProvider getProvider(String modelName) {
        String providerName = modelToProvider.get(modelName);
        if (providerName != null) {
            LlmProvider provider = providers.get(providerName);
            if (provider != null && provider.isAvailable()) return provider;
        }
        return findAvailableProvider();
    }

    public LlmProvider.LlmResponse chat(LlmProvider.LlmRequest request) {
        return getProvider(request.getModel()).chat(request);
    }

    public void chatStream(LlmProvider.LlmRequest request, LlmProvider.StreamCallback callback) {
        getProvider(request.getModel()).chatStream(request, callback);
    }

    public List<float[]> embeddings(List<String> texts) {
        Optional<LlmProvider> realProvider = providers.values().stream()
                .filter(p -> p.isAvailable() && !(p instanceof MockLlmProvider)).findFirst();
        return realProvider.orElse(mockLlmProvider).embeddings(texts);
    }

    public List<Map<String, Object>> listModels() {
        List<Map<String, Object>> modelList = new ArrayList<>();
        for (Map.Entry<String, LlmProvider> entry : providers.entrySet()) {
            Map<String, Object> info = new HashMap<>();
            info.put("provider", entry.getKey());
            info.put("available", entry.getValue().isAvailable());
            info.put("models", entry.getValue().listModels());
            modelList.add(info);
        }
        return modelList;
    }

    private LlmProvider findAvailableProvider() {
        Optional<LlmProvider> realProvider = providers.values().stream()
                .filter(p -> p.isAvailable() && !(p instanceof MockLlmProvider)).findFirst();
        if (realProvider.isPresent()) return realProvider.get();
        if (mockLlmProvider.isAvailable()) return mockLlmProvider;
        throw new RuntimeException("No available LLM provider (including mock)");
    }
}
