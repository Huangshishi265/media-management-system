package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 根据角色ID删除关联
     */
    default void deleteByRoleId(Long roleId) {
        this.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
    }

    /**
     * 根据菜单ID删除关联
     */
    default void deleteByMenuId(Long menuId) {
        this.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, menuId));
    }
}