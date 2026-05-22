package com.ruoyi.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP工具注册中心
 */
@Service
public class McpRegistry {

    private static final Logger log = LoggerFactory.getLogger(McpRegistry.class);
    private final Map<String, McpTool> tools = new ConcurrentHashMap<>();

    public void registerTool(McpTool tool) {
        tools.put(tool.name(), tool);
        log.info("Registered MCP tool: {}", tool.name());
    }

    public List<McpTool> discoverTools() { return new ArrayList<>(tools.values()); }

    public JsonNode executeTool(String name, JsonNode args) {
        McpTool tool = tools.get(name);
        if (tool == null) throw new RuntimeException("Tool not found: " + name);
        return tool.execute(args);
    }

    public List<Map<String, Object>> toOpenAiFunctions() {
        List<Map<String, Object>> functions = new ArrayList<>();
        for (McpTool tool : tools.values()) {
            functions.add(tool.toOpenAiFunction());
        }
        return functions;
    }
}
