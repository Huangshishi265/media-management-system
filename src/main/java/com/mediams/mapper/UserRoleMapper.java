package com.mediams.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mediams.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID删除关联
     */
    default void deleteByUserId(Long userId) {
        this.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
    }

    /**
     * 根据角色ID删除关联
     */
    default void deleteByRoleId(Long roleId) {
        this.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, roleId));
    }
}