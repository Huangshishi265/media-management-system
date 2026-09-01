package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单 Mapper
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 查询用户的菜单列表
     */
    @Select("SELECT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.status = 1 " +
            "ORDER BY m.sort ASC")
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的权限标识列表
     */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.status = 1 AND m.permission IS NOT NULL AND m.permission != ''")
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的菜单列表
     */
    @Select("SELECT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} AND m.deleted = 0 ORDER BY m.sort ASC")
    List<Menu> selectMenusByRoleId(@Param("roleId") Long roleId);
}