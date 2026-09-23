package com.mediams.service;

import com.mediams.dto.PageResult;
import com.mediams.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 更新用户
     */
    User updateUser(User user);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);

    /**
     * 分页查询用户
     */
    PageResult<User> pageList(Integer pageNum, Integer pageSize, String username, Integer status);

    /**
     * 更新用户状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 重置密码
     */
    boolean resetPassword(Long id, String newPassword);

    /**
     * 分配角色
     */
    boolean assignRoles(Long userId, Long[] roleIds);
}
