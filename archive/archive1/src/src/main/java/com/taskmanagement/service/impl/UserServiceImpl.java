package com.taskmanagement.service.impl;

import com.taskmanagement.dao.UserDAO;
import com.taskmanagement.dao.impl.UserDAOImpl;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
import com.taskmanagement.util.PasswordUtil;

import java.util.List;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User register(String username, String password, String email) {
        // 验证用户名和邮箱是否可用
        if (!isUsernameAvailable(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("邮箱已被使用");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encrypt(password)); // 密码加密
        user.setEmail(email);

        // 保存用户
        userDAO.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名不存在");
        }

        // 验证密码
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        return user;
    }

    @Override
    public User updateUser(User user) {
        // 验证用户是否存在
        User existingUser = userDAO.findById(user.getUserId());
        if (existingUser == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 如果修改了邮箱，需要验证新邮箱是否可用
        if (!existingUser.getEmail().equals(user.getEmail()) && !isEmailAvailable(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已被其他用户使用");
        }

        // 更新用户信息
        userDAO.update(user);
        return user;
    }

    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        // 获取用户信息
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encrypt(newPassword));
        return userDAO.update(user) > 0;
    }

    @Override
    public User getUserById(Integer userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        // 验证用户是否存在
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 删除用户
        return userDAO.delete(userId) > 0;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userDAO.findByUsername(username) == null;
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return userDAO.findByEmail(email) == null;
    }
} 