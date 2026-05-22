package com.ruoyi.ai.engine;

import java.util.List;
import java.util.Map;

/**
 * AI唱歌引擎接口
 */
public interface SingingEngine {
    byte[] synthesize(String lyrics, String midiPath, String style, Map<String, Object> params);
    boolean isAvailable();
    List<String> listStyles();
}
