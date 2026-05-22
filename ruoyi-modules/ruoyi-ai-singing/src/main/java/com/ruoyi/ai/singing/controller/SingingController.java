package com.ruoyi.ai.singing.controller;

import com.ruoyi.ai.singing.service.SingingService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/singing")
public class SingingController extends BaseController {
    @Autowired
    private SingingService singingService;

    @PostMapping("/synthesize")
    public AjaxResult synthesize(@RequestBody Map<String, Object> params) { return AjaxResult.success(singingService.synthesize(params)); }

    @GetMapping("/styles")
    public AjaxResult listStyles() { return AjaxResult.success(singingService.listStyles()); }
}
