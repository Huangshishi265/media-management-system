package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

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

    /** 权限列表（非数据库字段） */
    @TableField(exist = false)
    private List<Menu> menus;

    /** 权限ID列表（非数据库字段） */
    @TableField(exist = false)
    private List<Long> menuIds;
}