package com.ruoyi.ai.video.controller;

import com.ruoyi.ai.video.service.VideoService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/video")
public class VideoController extends BaseController {
    @Autowired
    private VideoService videoService;

    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String, Object> params) { return AjaxResult.success(videoService.generate(params)); }
}
