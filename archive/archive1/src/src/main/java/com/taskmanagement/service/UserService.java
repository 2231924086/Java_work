package com.taskmanagement.service;

import com.taskmanagement.model.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 注册成功的用户对象
     * @throws IllegalArgumentException 如果用户名已存在或邮箱已存在
     */
    User register(String username, String password, String email);

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象
     * @throws IllegalArgumentException 如果用户名或密码错误
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 如果用户不存在或邮箱已被其他用户使用
     */
    User updateUser(User user);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     * @throws IllegalArgumentException 如果旧密码错误
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    User getUserById(Integer userId);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    User getUserByEmail(String email);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     * @throws IllegalArgumentException 如果用户不存在
     */
    boolean deleteUser(Integer userId);

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 验证用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username);

    /**
     * 验证邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    boolean isEmailAvailable(String email);
} 