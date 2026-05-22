package com.ruoyi.ai.engine;

import java.util.Map;

/**
 * 视频生成引擎接口
 */
public interface VideoEngine {
    String generate(String prompt, Map<String, Object> params);
    boolean isAvailable();
}
