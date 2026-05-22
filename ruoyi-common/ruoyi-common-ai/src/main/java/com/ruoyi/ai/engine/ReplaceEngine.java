package com.ruoyi.ai.engine;

import java.util.Map;

/**
 * 人物替换引擎接口
 */
public interface ReplaceEngine {
    String replace(String sourceImagePath, String targetImagePath, Map<String, Object> params);
    boolean isAvailable();
}
