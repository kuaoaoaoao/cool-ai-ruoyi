package com.ruoyi.ai.deepfake.service;

import com.ruoyi.ai.engine.ReplaceEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeepfakeService {
    private final Map<String, ReplaceEngine> engines = new LinkedHashMap<>();

    @Autowired
    public DeepfakeService(List<ReplaceEngine> engineList) {
        for (ReplaceEngine engine : engineList) engines.put(engine.getClass().getSimpleName(), engine);
    }

    public Map<String, Object> replace(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", UUID.randomUUID().toString());
        result.put("status", "processing");
        return result;
    }

    public Map<String, Object> signAgreement(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("signed", true);
        result.put("userId", params.get("userId"));
        return result;
    }
}
