package com.ruoyi.ai.engine;

import java.util.Map;

/**
 * 声音克隆引擎接口
 */
public interface CloneEngine {
    byte[] synthesize(String text, String voiceModelId, Map<String, Object> params);
    boolean isAvailable();
}
