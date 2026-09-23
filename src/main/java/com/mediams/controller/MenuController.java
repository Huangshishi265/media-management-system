package com.mediams.controller;

import com.mediams.dto.Result;
import com.mediams.entity.Menu;
import com.mediams.entity.User;
import com.mediams.mapper.UserMapper;
import com.mediams.service.MenuService;
import com.mediams.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserMapper userMapper;

    /**
     * 查询菜单树
     */
    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return Result.success(menuTree);
    }

    /**
     * 查询当前用户的菜单
     */
    @GetMapping("/user")
    public Result<List<Menu>> getUserMenus() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            return Result.error(401, "未登录");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        List<Menu> menus = menuService.getMenusByUserId(user.getId());
        return Result.success(menus);
    }

    /**
     * 根据ID查询菜单
     */
    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    /**
     * 创建菜单
     */
    @PostMapping
    public Result<Menu> create(@RequestBody Menu menu) {
        Menu createdMenu = menuService.createMenu(menu);
        return Result.success("创建成功", createdMenu);
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    public Result<Menu> update(@PathVariable Long id, @RequestBody Menu menu) {
        menu.setId(id);
        Menu updatedMenu = menuService.updateMenu(menu);
        return Result.success("更新成功", updatedMenu);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success("删除成功");
    }

    /**
     * 查询所有菜单列表
     */
    @GetMapping("/list")
    public Result<List<Menu>> list() {
        List<Menu> menus = menuService.list();
        return Result.success(menus);
    }
}