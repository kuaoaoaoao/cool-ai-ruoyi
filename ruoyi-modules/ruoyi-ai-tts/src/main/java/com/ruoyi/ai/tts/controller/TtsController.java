package com.ruoyi.ai.tts.controller;

import com.ruoyi.ai.tts.service.TtsService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tts")
public class TtsController extends BaseController {
    @Autowired
    private TtsService ttsService;

    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesize(@RequestBody Map<String, Object> params) {
        String text = (String) params.get("text");
        String voice = (String) params.getOrDefault("voice", "zh-CN-XiaoxiaoNeural");
        byte[] audio = ttsService.synthesize(text, voice, params);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tts_output.mp3").body(audio);
    }

    @GetMapping("/voices")
    public AjaxResult listVoices() { return AjaxResult.success(ttsService.listVoices()); }
}
