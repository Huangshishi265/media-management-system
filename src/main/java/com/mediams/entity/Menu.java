package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单路径 */
    private String path;

    /** 菜单图标 */
    private String icon;

    /** 权限标识 */
    private String permission;

    /** 菜单类型：0-目录，1-菜单，2-按钮 */
    private Integer menuType;

    /** 排序 */
    private Integer sort;

    /** 是否可见：0-隐藏，1-显示 */
    private Integer visible;

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

    /** 子菜单列表（非数据库字段） */
    @TableField(exist = false)
    private List<Menu> children;
}