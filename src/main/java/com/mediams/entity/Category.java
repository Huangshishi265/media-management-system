package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("media_category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父分类ID */
    private Long parentId;

    /** 分类名称 */
    private String categoryName;

    /** 分类编码 */
    private String categoryCode;

    /** 分类图标 */
    private String icon;

    /** 排序 */
    private Integer sort;

    /** 状态：0-禁用，1-正常 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;

    /** 备注 */
    private String remark;

    /** 子分类列表（非数据库字段） */
    @TableField(exist = false)
    private List<Category> children;

    /** 素材数量（非数据库字段） */
    @TableField(exist = false)
    private Integer mediaCount;
}