package com.mediams.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mediams.dto.PageResult;
import com.mediams.entity.User;
import com.mediams.entity.UserRole;
import com.mediams.exception.BusinessException;
import com.mediams.mapper.UserMapper;
import com.mediams.mapper.UserRoleMapper;
import com.mediams.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (user.getEmail() != null && userMapper.selectByEmail(user.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了密码，需要加密
        if (user.getPassword() != null && !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public PageResult<User> pageList(Integer pageNum, Integer pageSize, String username, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long id, String newPassword) {
        User user = new User();
        user.setId(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional
    public boolean assignRoles(Long userId, Long[] roleIds) {
        // 删除原有角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 添加新的角色关联
        if (roleIds != null && roleIds.length > 0) {
            Arrays.stream(roleIds).forEach(roleId -> {
                UserRole userRole = UserRole.builder()
                        .userId(userId)
                        .roleId(roleId)
                        .build();
                userRoleMapper.insert(userRole);
            });
        }
        
        return true;
    }
}