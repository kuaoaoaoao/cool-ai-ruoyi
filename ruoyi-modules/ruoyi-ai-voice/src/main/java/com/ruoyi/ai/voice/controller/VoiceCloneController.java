package com.ruoyi.ai.voice.controller;

import com.ruoyi.ai.voice.service.VoiceCloneService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/voice")
public class VoiceCloneController extends BaseController {
    @Autowired
    private VoiceCloneService voiceCloneService;

    @PostMapping("/models")
    public AjaxResult createModel(@RequestBody Map<String, Object> params) { return AjaxResult.success(voiceCloneService.createModel(params)); }

    @PostMapping("/synthesize")
    public AjaxResult synthesize(@RequestBody Map<String, Object> params) { return AjaxResult.success(voiceCloneService.synthesize(params)); }
}
