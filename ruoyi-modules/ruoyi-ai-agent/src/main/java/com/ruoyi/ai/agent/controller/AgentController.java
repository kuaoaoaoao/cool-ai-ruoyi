package com.ruoyi.ai.agent.controller;

import com.ruoyi.ai.agent.service.AgentService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI Agent Controller
 */
@RestController
@RequestMapping("/agent")
public class AgentController extends BaseController {

    @Autowired
    private AgentService agentService;

    /** 执行Agent任务 */
    @PostMapping("/execute")
    public AjaxResult execute(@RequestBody Map<String, Object> params) {
        String task = (String) params.get("task");
        String model = (String) params.getOrDefault("model", "mock-llm");
        return AjaxResult.success(agentService.execute(task, model));
    }

    /** 获取可用MCP工具 */
    @GetMapping("/tools")
    public AjaxResult listTools() {
        return AjaxResult.success(agentService.listTools());
    }
}
