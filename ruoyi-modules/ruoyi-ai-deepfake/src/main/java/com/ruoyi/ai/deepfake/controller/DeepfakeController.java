package com.ruoyi.ai.deepfake.controller;

import com.ruoyi.ai.deepfake.service.DeepfakeService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/deepfake")
public class DeepfakeController extends BaseController {
    @Autowired
    private DeepfakeService deepfakeService;

    @PostMapping("/replace")
    public AjaxResult replace(@RequestBody Map<String, Object> params) { return AjaxResult.success(deepfakeService.replace(params)); }

    @PostMapping("/agreement")
    public AjaxResult signAgreement(@RequestBody Map<String, Object> params) { return AjaxResult.success(deepfakeService.signAgreement(params)); }
}
