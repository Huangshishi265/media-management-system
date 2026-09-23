package com.mediams.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 智谱 BigModel 客户端（OpenAI 兼容协议）
 *
 * <p>产品设计要点：
 * <ul>
 *   <li>超时 + 异常统一抛出，由上层（AiAnalyzeService）降级，保证 AI 失败不阻塞主流程</li>
 *   <li>max_tokens / temperature 做成配置，控制成本与输出确定性</li>
 *   <li>未配置 key 时 isConfigured() 返回 false，上层直接走降级</li>
 * </ul>
 */
@Slf4j
@Component
public class ZhipuAiClient {

    @Value("${ai.zhipu.api-key:}")
    private String apiKey;

    @Value("${ai.zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String baseUrl;

    @Value("${ai.zhipu.vision-model:glm-4v-flash}")
    private String visionModel;

    @Value("${ai.zhipu.text-model:glm-4-flash}")
    private String textModel;

    @Value("${ai.zhipu.timeout-ms:15000}")
    private int timeoutMs;

    @Value("${ai.zhipu.max-tokens:512}")
    private int maxTokens;

    /** 是否已正确配置 API Key（占位符视为未配置） */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !"your_zhipu_api_key".equals(apiKey);
    }

    /**
     * 纯文本对话
     */
    public String chatText(String systemPrompt, String userText) {
        JSONArray messages = JSONUtil.createArray();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(JSONUtil.createObj().set("role", "system").set("content", systemPrompt));
        }
        messages.add(JSONUtil.createObj().set("role", "user").set("content", userText));
        return chat(messages, textModel);
    }

    /**
     * 视觉理解（图片 base64 + 文字）
     */
    public String chatVision(String systemPrompt, String prompt, String base64Image, String mimeType) {
        JSONArray messages = JSONUtil.createArray();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(JSONUtil.createObj().set("role", "system").set("content", systemPrompt));
        }
        JSONArray userContent = JSONUtil.createArray();
        userContent.add(JSONUtil.createObj().set("type", "text").set("text", prompt));
        userContent.add(JSONUtil.createObj()
                .set("type", "image_url")
                .set("image_url", JSONUtil.createObj()
                        .set("url", "data:" + mimeType + ";base64," + base64Image)));
        messages.add(JSONUtil.createObj().set("role", "user").set("content", userContent));
        return chat(messages, visionModel);
    }

    private String chat(JSONArray messages, String model) {
        JSONObject body = JSONUtil.createObj()
                .set("model", model)
                .set("messages", messages)
                .set("max_tokens", maxTokens)
                .set("temperature", 0.3);

        try (HttpResponse resp = HttpRequest.post(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .timeout(timeoutMs)
                .body(body.toString())
                .execute()) {

            String respBody = resp.body();
            if (resp.getStatus() != 200) {
                log.error("智谱 API 调用失败: status={}, body={}", resp.getStatus(), respBody);
                throw new IllegalStateException("AI 接口返回错误: HTTP " + resp.getStatus());
            }

            JSONObject json = JSONUtil.parseObj(respBody);
            JSONArray choices = json.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IllegalStateException("AI 接口未返回内容");
            }
            return choices.getJSONObject(0).getJSONObject("message").getStr("content");
        }
    }
}
