package com.mediams.ai;

import lombok.Data;

import java.util.List;

/**
 * AI 图片分析结果
 *
 * <p>categoryName 为模型建议的分类名；categoryId 为规则映射到现有分类树后的结果（映射失败为 null，前端回退为「请手动选择」）。
 */
@Data
public class AiTagResult {

    /** 建议标题 */
    private String title;

    /** 建议描述 */
    private String description;

    /** 建议标签 */
    private List<String> tags;

    /** 模型建议的分类名 */
    private String categoryName;

    /** 规则映射后的分类ID（可能为 null） */
    private Long categoryId;

    /** 是否降级（未配置 key / 调用失败），前端据此提示用户手动填写 */
    private boolean fallback;

    /** 降级或失败时的提示信息 */
    private String fallbackMessage;
}
