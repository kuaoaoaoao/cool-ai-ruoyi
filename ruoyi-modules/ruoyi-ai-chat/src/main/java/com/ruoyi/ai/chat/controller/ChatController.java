package com.ruoyi.ai.chat.controller;

import com.ruoyi.ai.chat.service.ChatService;
import com.ruoyi.ai.llm.LlmProvider;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI对话Controller
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends BaseController {

    @Autowired
    private ChatService chatService;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /** 同步对话 */
    @PostMapping("/message")
    public AjaxResult chat(@RequestBody Map<String, Object> params) {
        String message = (String) params.get("message");
        String conversationId = (String) params.get("conversationId");
        String model = (String) params.getOrDefault("model", "mock-llm");
        return AjaxResult.success(chatService.chat(message, conversationId, model));
    }

    /** SSE流式对话 */
    @GetMapping("/stream")
    public SseEmitter chatStream(@RequestParam String message,
                                  @RequestParam(required = false) String conversationId,
                                  @RequestParam(defaultValue = "mock-llm") String model) {
        SseEmitter emitter = new SseEmitter(120000L);
        executor.execute(() -> {
            try {
                chatService.chatStream(message, conversationId, model, new LlmProvider.StreamCallback() {
                    @Override
                    public void onToken(String token) {
                        try { emitter.send(SseEmitter.event().data(token)); } catch (Exception ignored) {}
                    }
                    @Override
                    public void onDone(LlmProvider.LlmResponse response) {
                        try { emitter.send(SseEmitter.event().name("done").data("[DONE]")); emitter.complete(); } catch (Exception ignored) {}
                    }
                    @Override
                    public void onError(Exception e) {
                        emitter.completeWithError(e);
                    }
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    /** 获取可用模型列表 */
    @GetMapping("/models")
    public AjaxResult listModels() {
        return AjaxResult.success(chatService.listModels());
    }
}
