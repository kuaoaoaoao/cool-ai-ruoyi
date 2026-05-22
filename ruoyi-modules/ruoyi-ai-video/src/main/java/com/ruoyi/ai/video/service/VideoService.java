package com.ruoyi.ai.video.service;

import com.ruoyi.ai.engine.VideoEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VideoService {
    private final Map<String, VideoEngine> engines = new LinkedHashMap<>();

    @Autowired
    public VideoService(List<VideoEngine> engineList) {
        for (VideoEngine engine : engineList) engines.put(engine.getClass().getSimpleName(), engine);
    }

    public Map<String, Object> generate(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", UUID.randomUUID().toString());
        result.put("status", "processing");
        return result;
    }
}
