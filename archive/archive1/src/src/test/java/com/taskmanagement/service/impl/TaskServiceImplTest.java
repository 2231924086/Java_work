package com.taskmanagement.service.impl;

import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.model.Category;
import com.taskmanagement.model.Task;
import com.taskmanagement.model.User;
import com.taskmanagement.service.CategoryService;
import com.taskmanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Category testCategory;
    private Task testTask;
    private Date testDate;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setName("测试分类");

        testDate = new Date();
        testTask = new Task();
        testTask.setTaskId(1);
        testTask.setUserId(testUser.getUserId());
        testTask.setCategoryId(testCategory.getCategoryId());
        testTask.setTitle("测试任务");
        testTask.setContent("测试内容");
        testTask.setPriority(2);
        testTask.setDueDate(testDate);
        testTask.setStatus(0);
    }

    @Test
    void createTask_Success() {
        // 准备测试数据
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryService.getCategory(anyInt())).thenReturn(testCategory);
        when(taskDAO.insert(any(Task.class))).thenReturn(1);

        // 执行测试
        Task result = taskService.createTask(
            testUser.getUserId(),
            testCategory.getCategoryId(),
            testTask.getTitle(),
            testTask.getContent(),
            testTask.getPriority(),
            testTask.getDueDate()
        );

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testCategory.getCategoryId(), result.getCategoryId());
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getContent(), result.getContent());
        assertEquals(testTask.getPriority(), result.getPriority());
        assertEquals(testTask.getDueDate(), result.getDueDate());
        assertEquals(0, result.getStatus());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(categoryService).getCategory(testCategory.getCategoryId());
        verify(taskDAO).insert(any(Task.class));
    }

    @Test
    void createTask_InvalidPriority() {
        // 准备测试数据
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryService.getCategory(anyInt())).thenReturn(testCategory);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            taskService.createTask(
                testUser.getUserId(),
                testCategory.getCategoryId(),
                testTask.getTitle(),
                testTask.getContent(),
                4, // 无效的优先级
                testTask.getDueDate()
            )
        );
    }

    @Test
    void updateTask_Success() {
        // 准备测试数据
        when(taskDAO.findById(anyInt())).thenReturn(testTask);
        when(categoryService.getCategory(anyInt())).thenReturn(testCategory);
        when(taskDAO.update(any(Task.class))).thenReturn(1);

        // 执行测试
        Task result = taskService.updateTask(
            testTask.getTaskId(),
            "新标题",
            "新内容",
            3,
            testDate,
            testCategory.getCategoryId()
        );

        // 验证结果
        assertNotNull(result);
        assertEquals("新标题", result.getTitle());
        assertEquals("新内容", result.getContent());
        assertEquals(3, result.getPriority());
        assertEquals(testCategory.getCategoryId(), result.getCategoryId());

        // 验证方法调用
        verify(taskDAO).findById(testTask.getTaskId());
        verify(categoryService).getCategory(testCategory.getCategoryId());
        verify(taskDAO).update(any(Task.class));
    }

    @Test
    void updateTaskStatus_Success() {
        // 准备测试数据
        when(taskDAO.findById(anyInt())).thenReturn(testTask);
        when(taskDAO.updateStatus(anyInt(), anyInt())).thenReturn(1);

        // 执行测试
        Task result = taskService.updateTaskStatus(testTask.getTaskId(), 1);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getStatus());

        // 验证方法调用
        verify(taskDAO).findById(testTask.getTaskId());
        verify(taskDAO).updateStatus(testTask.getTaskId(), 1);
    }

    @Test
    void updateTaskStatus_InvalidStatus() {
        // 准备测试数据
        when(taskDAO.findById(anyInt())).thenReturn(testTask);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            taskService.updateTaskStatus(testTask.getTaskId(), 2)
        );
    }

    @Test
    void getTasksByCategory_Success() {
        // 准备测试数据
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryService.getCategory(anyInt())).thenReturn(testCategory);
        when(taskDAO.findByUserIdAndCategoryId(anyInt(), anyInt())).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getTasksByCategory(
            testUser.getUserId(),
            testCategory.getCategoryId()
        );

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(testTask.getTaskId(), result.get(0).getTaskId());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(categoryService).getCategory(testCategory.getCategoryId());
        verify(taskDAO).findByUserIdAndCategoryId(testUser.getUserId(), testCategory.getCategoryId());
    }

    @Test
    void getUpcomingTasks_Success() {
        // 准备测试数据
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(taskDAO.findUpcomingTasks(anyInt())).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.getUpcomingTasks(testUser.getUserId());

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(testTask.getTaskId(), result.get(0).getTaskId());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(taskDAO).findUpcomingTasks(testUser.getUserId());
    }

    @Test
    void searchTasks_Success() {
        // 准备测试数据
        String keyword = "测试";
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(taskDAO.searchTasks(anyInt(), anyString())).thenReturn(expectedTasks);

        // 执行测试
        List<Task> result = taskService.searchTasks(testUser.getUserId(), keyword);

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(testTask.getTaskId(), result.get(0).getTaskId());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(taskDAO).searchTasks(testUser.getUserId(), keyword);
    }

    @Test
    void deleteTask_Success() {
        // 准备测试数据
        when(taskDAO.findById(anyInt())).thenReturn(testTask);
        when(taskDAO.delete(anyInt())).thenReturn(1);

        // 执行测试
        boolean result = taskService.deleteTask(testTask.getTaskId());

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(taskDAO).findById(testTask.getTaskId());
        verify(taskDAO).delete(testTask.getTaskId());
    }

    @Test
    void deleteTask_TaskNotFound() {
        // 准备测试数据
        when(taskDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            taskService.deleteTask(testTask.getTaskId())
        );
    }
} 