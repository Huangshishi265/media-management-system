package com.mediams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mediams.entity.Menu;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.MenuMapper;
import com.mediams.mapper.RoleMenuMapper;
import com.mediams.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getStatus, 1)
                .orderByAsc(Menu::getSort));

        // 构建树形结构
        return buildMenuTree(allMenus, 0L);
    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(buildMenuTree(menus, menu.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Menu createMenu(Menu menu) {
        menu.setStatus(1);
        menu.setVisible(1);
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    @Transactional
    public Menu updateMenu(Menu menu) {
        Menu existingMenu = menuMapper.selectById(menu.getId());
        if (existingMenu == null) {
            throw new BusinessException("菜单不存在");
        }

        menuMapper.updateById(menu);
        return menu;
    }

    @Override
    @Transactional
    public boolean deleteMenu(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, id);
        if (menuMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子菜单，无法删除");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByMenuId(id);
        
        return menuMapper.deleteById(id) > 0;
    }

    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return menuMapper.selectPermissionsByUserId(userId);
    }
}