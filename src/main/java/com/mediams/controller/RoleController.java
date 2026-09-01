package com.mediams.controller;

import com.mediams.dto.PageResult;
import com.mediams.dto.Result;
import com.mediams.entity.Role;
import com.mediams.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    public Result<PageResult<Role>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        PageResult<Role> pageResult = roleService.pageList(pageNum, pageSize, roleName, status);
        return Result.success(pageResult);
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return Result.success("创建成功", createdRole);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        Role updatedRole = roleService.updateRole(role);
        return Result.success("更新成功", updatedRole);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功");
    }

    /**
     * 分配菜单权限
     */
    @PostMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody Long[] menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success("菜单权限分配成功");
    }

    /**
     * 查询角色的菜单列表
     */
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getMenus(@PathVariable Long id) {
        // TODO: 实现获取角色菜单列表
        return Result.success(null);
    }
}