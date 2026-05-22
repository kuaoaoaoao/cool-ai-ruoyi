package com.ruoyi.ai.singing.service;

import com.ruoyi.ai.engine.SingingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SingingService {
    private final Map<String, SingingEngine> engines = new LinkedHashMap<>();

    @Autowired
    public SingingService(List<SingingEngine> engineList) {
        for (SingingEngine engine : engineList) engines.put(engine.getClass().getSimpleName(), engine);
    }

    public Map<String, Object> synthesize(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", UUID.randomUUID().toString());
        result.put("status", "processing");
        return result;
    }

    public List<String> listStyles() {
        return engines.values().stream().filter(SingingEngine::isAvailable).findFirst()
                .map(SingingEngine::listStyles).orElse(Collections.emptyList());
    }
}
