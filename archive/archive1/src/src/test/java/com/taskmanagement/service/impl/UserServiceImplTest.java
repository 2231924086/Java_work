package com.taskmanagement.service.impl;

import com.taskmanagement.dao.UserDAO;
import com.taskmanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
    }

    @Test
    void register_Success() {
        // 准备测试数据
        when(userDAO.findByUsername(anyString())).thenReturn(null);
        when(userDAO.insert(any(User.class))).thenReturn(1);

        // 执行测试
        User result = userService.register(
                testUser.getUsername(),
                testUser.getPassword(),
                testUser.getEmail(),
                testUser.getPhone());

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getPhone(), result.getPhone());
        assertNotEquals(testUser.getPassword(), result.getPassword()); // 密码应该被加密

        // 验证方法调用
        verify(userDAO).findByUsername(testUser.getUsername());
        verify(userDAO).insert(any(User.class));
    }

    @Test
    void register_DuplicateUsername() {
        // 准备测试数据
        when(userDAO.findByUsername(anyString())).thenReturn(testUser);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> userService.register(
                testUser.getUsername(),
                testUser.getPassword(),
                testUser.getEmail(),
                testUser.getPhone()));

        // 验证方法调用
        verify(userDAO).findByUsername(testUser.getUsername());
        verify(userDAO, never()).insert(any(User.class));
    }

    @Test
    void login_Success() {
        // 准备测试数据
        when(userDAO.findByUsername(anyString())).thenReturn(testUser);

        // 执行测试
        User result = userService.login(testUser.getUsername(), testUser.getPassword());

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getUsername(), result.getUsername());

        // 验证方法调用
        verify(userDAO).findByUsername(testUser.getUsername());
    }

    @Test
    void login_InvalidCredentials() {
        // 准备测试数据
        when(userDAO.findByUsername(anyString())).thenReturn(testUser);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> userService.login(testUser.getUsername(), "wrongpassword"));

        // 验证方法调用
        verify(userDAO).findByUsername(testUser.getUsername());
    }

    @Test
    void updateUser_Success() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(testUser);
        when(userDAO.update(any(User.class))).thenReturn(1);

        // 执行测试
        User result = userService.updateUser(
                testUser.getUserId(),
                "newemail@example.com",
                "13900139000");

        // 验证结果
        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("13900139000", result.getPhone());

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO).update(any(User.class));
    }

    @Test
    void updateUser_UserNotFound() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(
                testUser.getUserId(),
                "newemail@example.com",
                "13900139000"));

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    void updatePassword_Success() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(testUser);
        when(userDAO.update(any(User.class))).thenReturn(1);

        // 执行测试
        boolean result = userService.changePassword(
                testUser.getUserId(),
                testUser.getPassword(),
                "newpassword123");

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO).update(any(User.class));
    }

    @Test
    void updatePassword_WrongOldPassword() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(testUser);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(
                testUser.getUserId(),
                "wrongpassword",
                "newpassword123"));

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO, never()).update(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(testUser);
        when(userDAO.delete(anyInt())).thenReturn(1);

        // 执行测试
        boolean result = userService.deleteUser(testUser.getUserId());

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO).delete(testUser.getUserId());
    }

    @Test
    void deleteUser_UserNotFound() {
        // 准备测试数据
        when(userDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(testUser.getUserId()));

        // 验证方法调用
        verify(userDAO).findById(testUser.getUserId());
        verify(userDAO, never()).delete(anyInt());
    }

    @Test
    void getAllUsers_Success() {
        // 准备测试数据
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userDAO.findAll()).thenReturn(expectedUsers);

        // 执行测试
        List<User> result = userService.getAllUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedUsers.size(), result.size());
        assertEquals(testUser.getUserId(), result.get(0).getUserId());

        // 验证方法调用
        verify(userDAO).findAll();
    }
}