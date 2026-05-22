package com.ruoyi.ai.knowledge.controller;

import com.ruoyi.ai.knowledge.service.KnowledgeService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController extends BaseController {
    @Autowired
    private KnowledgeService knowledgeService;

    @PostMapping("/bases")
    public AjaxResult createBase(@RequestBody Map<String, Object> params) { return AjaxResult.success(knowledgeService.createBase(params)); }

    @GetMapping("/bases")
    public AjaxResult listBases() { return AjaxResult.success(knowledgeService.listBases()); }

    @PostMapping("/bases/{baseId}/documents")
    public AjaxResult uploadDocument(@PathVariable String baseId, @RequestBody Map<String, Object> params) { return AjaxResult.success(knowledgeService.uploadDocument(baseId, params)); }

    @PostMapping("/bases/{baseId}/query")
    public AjaxResult query(@PathVariable String baseId, @RequestBody Map<String, Object> params) { return AjaxResult.success(knowledgeService.query(baseId, params)); }
}
