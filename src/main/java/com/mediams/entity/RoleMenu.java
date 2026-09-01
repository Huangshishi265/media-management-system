package com.mediams.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 角色-菜单关联表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_role_menu")
public class RoleMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色ID */
    private Long roleId;

    /** 菜单ID */
    private Long menuId;
}