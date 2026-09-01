package com.mediams.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mediams.entity.Menu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查询菜单树
     */
    List<Menu> getMenuTree();

    /**
     * 创建菜单
     */
    Menu createMenu(Menu menu);

    /**
     * 更新菜单
     */
    Menu updateMenu(Menu menu);

    /**
     * 删除菜单
     */
    boolean deleteMenu(Long id);

    /**
     * 查询用户的菜单列表
     */
    List<Menu> getMenusByUserId(Long userId);

    /**
     * 查询用户的权限标识列表
     */
    List<String> getPermissionsByUserId(Long userId);
}