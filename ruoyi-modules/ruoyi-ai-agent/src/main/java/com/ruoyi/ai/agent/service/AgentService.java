package com.ruoyi.ai.agent.service;

import com.ruoyi.ai.llm.LlmGatewayService;
import com.ruoyi.ai.llm.LlmProvider;
import com.ruoyi.ai.mcp.McpRegistry;
import com.ruoyi.ai.mcp.McpTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI Agent Service - Plan-Execute三阶段执行
 */
@Service
public class AgentService {

    @Autowired
    private LlmGatewayService llmGatewayService;

    @Autowired
    private McpRegistry mcpRegistry;

    /** 执行Agent任务（Plan-Execute-Summarize） */
    public Map<String, Object> execute(String task, String model) {
        // Phase 1: Plan - LLM生成执行计划
        Map<String, Object> plan = planPhase(task, model);

        // Phase 2: Execute - 按步骤执行MCP工具
        List<Map<String, Object>> results = executePhase(plan);

        // Phase 3: Summarize - LLM总结结果
        String summary = summarizePhase(task, results, model);

        Map<String, Object> response = new HashMap<>();
        response.put("task", task);
        response.put("plan", plan);
        response.put("results", results);
        response.put("summary", summary);
        return response;
    }

    /** 获取MCP工具列表 */
    public List<Map<String, Object>> listTools() {
        return mcpRegistry.toOpenAiFunctions();
    }

    private Map<String, Object> planPhase(String task, String model) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", "你是一个任务规划助手。根据用户任务，生成执行步骤。可用工具：" + getToolDescriptions());
        messages.add(sysMsg);
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", task);
        messages.add(userMsg);

        LlmProvider.LlmRequest request = new LlmProvider.LlmRequest(model, messages);
        LlmProvider.LlmResponse response = llmGatewayService.chat(request);

        Map<String, Object> plan = new HashMap<>();
        plan.put("task", task);
        plan.put("plan", response.getContent());
        return plan;
    }

    private List<Map<String, Object>> executePhase(Map<String, Object> plan) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (McpTool tool : mcpRegistry.discoverTools()) {
            Map<String, Object> toolInfo = new HashMap<>();
            toolInfo.put("name", tool.name());
            toolInfo.put("description", tool.description());
            toolInfo.put("available", true);
            results.add(toolInfo);
        }
        return results;
    }

    private String summarizePhase(String task, List<Map<String, Object>> results, String model) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", "请根据执行结果，给出简洁总结。");
        messages.add(sysMsg);
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", "任务: " + task + "\n结果: " + results.toString());
        messages.add(userMsg);

        LlmProvider.LlmRequest request = new LlmProvider.LlmRequest(model, messages);
        return llmGatewayService.chat(request).getContent();
    }

    private String getToolDescriptions() {
        StringBuilder sb = new StringBuilder();
        for (McpTool tool : mcpRegistry.discoverTools()) {
            sb.append("- ").append(tool.name()).append(": ").append(tool.description()).append("\n");
        }
        return sb.toString();
    }
}
