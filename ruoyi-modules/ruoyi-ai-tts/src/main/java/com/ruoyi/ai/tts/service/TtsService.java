package com.ruoyi.ai.tts.service;

import com.ruoyi.ai.engine.TtsEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TtsService {
    private final Map<String, TtsEngine> engines = new LinkedHashMap<>();

    @Autowired
    public TtsService(List<TtsEngine> engineList) {
        for (TtsEngine engine : engineList) engines.put(engine.getClass().getSimpleName(), engine);
    }

    public byte[] synthesize(String text, String voice, Map<String, Object> params) {
        TtsEngine engine = engines.values().stream().filter(TtsEngine::isAvailable).findFirst()
                .orElseThrow(() -> new RuntimeException("No available TTS engine"));
        return engine.synthesize(text, voice, params);
    }

    public List<String> listVoices() {
        return engines.values().stream().filter(TtsEngine::isAvailable).findFirst()
                .map(TtsEngine::listVoices).orElse(Collections.emptyList());
    }
}
