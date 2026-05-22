package com.ruoyi.ai.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/**
 * MCP工具接口 - Agent可调用的工具
 */
public interface McpTool {

    /** 工具名称 */
    String name();

    /** 工具描述 */
    String description();

    /** 参数JSON Schema */
    JsonNode parameters();

    /** 执行工具 */
    JsonNode execute(JsonNode args);

    /** 转为OpenAI Function Calling格式 */
    Map<String, Object> toOpenAiFunction();
}
