package com.ruoyi.ai.engine;

import java.util.List;
import java.util.Map;

/**
 * TTS语音合成引擎接口
 */
public interface TtsEngine {
    byte[] synthesize(String text, String voice, Map<String, Object> params);
    boolean isAvailable();
    List<String> listVoices();
}
