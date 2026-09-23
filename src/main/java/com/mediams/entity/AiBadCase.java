package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 打标签 bad case 记录实体
 *
 * <p>产品迭代闭环的核心数据：记录「AI 原始输出」与「用户最终值」，据此计算采纳率、
 * 沉淀被改掉的样本用于调 prompt。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("ai_bad_case")
public class AiBadCase {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联素材ID（可选） */
    private Long mediaId;

    /** AI 建议标题 */
    private String aiTitle;

    /** AI 建议描述 */
    private String aiDescription;

    /** AI 建议标签（逗号分隔） */
    private String aiTags;

    /** AI 建议分类名 */
    private String aiCategory;

    /** 用户最终标题 */
    private String finalTitle;

    /** 用户最终描述 */
    private String finalDescription;

    /** 用户最终标签（逗号分隔） */
    private String finalTags;

    /** 用户最终分类ID */
    private Long finalCategoryId;

    /** 是否采纳 AI：1-采纳，0-用户修改 */
    private Integer adopted;

    /** 记录时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
