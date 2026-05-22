package com.ruoyi.ai.voice.service;

import com.ruoyi.ai.engine.CloneEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoiceCloneService {
    private final Map<String, CloneEngine> engines = new LinkedHashMap<>();

    @Autowired
    public VoiceCloneService(List<CloneEngine> engineList) {
        for (CloneEngine engine : engineList) engines.put(engine.getClass().getSimpleName(), engine);
    }

    public Map<String, Object> createModel(Map<String, Object> params) {
        Map<String, Object> model = new HashMap<>(params);
        model.put("id", UUID.randomUUID().toString());
        model.put("status", "training");
        return model;
    }

    public Map<String, Object> synthesize(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", UUID.randomUUID().toString());
        result.put("status", "processing");
        result.put("message", "语音合成任务已提交");
        return result;
    }
}
