package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 素材实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("media")
public class Media {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 素材标题 */
    private String title;

    /** 素材描述 */
    private String description;

    /** 素材类型：image/video/audio/document */
    private String mediaType;

    /** 分类ID */
    private Long categoryId;

    /** 文件路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

    /** 原始文件名 */
    private String originalName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件扩展名 */
    private String fileExt;

    /** 封面图路径 */
    private String coverPath;

    /** 标签（逗号分隔） */
    private String tags;

    /** 下载次数 */
    private Integer downloadCount;

    /** 浏览次数 */
    private Integer viewCount;

    /** 状态：0-下架，1-上架 */
    private Integer status;

    /** 是否推荐：0-否，1-是 */
    private Integer isRecommend;

    /** 创建人ID */
    private Long createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 分类名称（非数据库字段） */
    @TableField(exist = false)
    private String categoryName;

    /** 创建人名称（非数据库字段） */
    @TableField(exist = false)
    private String createByName;
}