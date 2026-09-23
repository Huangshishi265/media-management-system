package com.mediams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mediams.dto.PageResult;
import com.mediams.entity.Role;
import com.mediams.entity.RoleMenu;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.RoleMapper;
import com.mediams.mapper.RoleMenuMapper;
import com.mediams.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public PageResult<Role> pageList(Integer pageNum, Integer pageSize, String roleName, Integer status) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(Role::getRoleName, roleName);
        }
        
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }

        wrapper.orderByAsc(Role::getSort);

        Page<Role> page = roleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        // 检查角色编码是否存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, role.getRoleCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        role.setStatus(1);
        roleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Role role) {
        Role existingRole = roleMapper.selectById(role.getId());
        if (existingRole == null) {
            throw new BusinessException("角色不存在");
        }

        roleMapper.updateById(role);
        return role;
    }

    @Override
    @Transactional
    public boolean deleteRole(Long id) {
        // 删除角色菜单关联
        roleMenuMapper.deleteByRoleId(id);
        
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional
    public boolean assignMenus(Long roleId, Long[] menuIds) {
        // 删除原有菜单关联
        roleMenuMapper.deleteByRoleId(roleId);
        
        // 添加新的菜单关联
        if (menuIds != null && menuIds.length > 0) {
            Arrays.stream(menuIds).forEach(menuId -> {
                RoleMenu roleMenu = RoleMenu.builder()
                        .roleId(roleId)
                        .menuId(menuId)
                        .build();
                roleMenuMapper.insert(roleMenu);
            });
        }

        return true;
    }

    @Override
    public List<Long> getMenusByRoleId(Long roleId) {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        return roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
    }
}