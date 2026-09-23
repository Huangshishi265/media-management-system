package com.mediams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mediams.dto.PageResult;
import com.mediams.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色
     */
    PageResult<Role> pageList(Integer pageNum, Integer pageSize, String roleName, Integer status);

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 查询用户的角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 分配菜单权限
     */
    boolean assignMenus(Long roleId, Long[] menuIds);

    /**
     * 查询角色的菜单ID列表
     */
    List<Long> getMenusByRoleId(Long roleId);
}