package com.taskmanagement.dao;

import com.taskmanagement.model.User;

/**
 * 用户数据访问接口
 */
public interface UserDAO extends BaseDAO<User, Integer> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户对象，如果不存在返回null
     */
    User findByEmail(String email);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 影响的行数
     */
    int updateLastLogin(Integer userId);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态（true:活跃，false:禁用）
     * @return 影响的行数
     */
    int updateStatus(Integer userId, boolean status);

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 影响的行数
     */
    int updatePassword(Integer userId, String newPassword);
} 