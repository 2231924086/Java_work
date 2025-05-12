package com.taskmanagement.service.impl;

import com.taskmanagement.dao.CategoryDAO;
import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.model.Category;
import com.taskmanagement.model.User;
import com.taskmanagement.service.UserService;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private TaskDAO taskDAO;

    @Mock
    private UserService userService;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setUserId(testUser.getUserId());
        testCategory.setName("测试分类");
        testCategory.setDescription("测试分类描述");
    }

    @Test
    void createCategory_Success() {
        // 准备测试数据
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryDAO.findByNameAndUserId(anyString(), anyInt())).thenReturn(null);
        when(categoryDAO.insert(any(Category.class))).thenReturn(1);

        // 执行测试
        Category result = categoryService.createCategory(
            testUser.getUserId(),
            testCategory.getName(),
            testCategory.getDescription()
        );

        // 验证结果
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testCategory.getName(), result.getName());
        assertEquals(testCategory.getDescription(), result.getDescription());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(categoryDAO).findByNameAndUserId(testCategory.getName(), testUser.getUserId());
        verify(categoryDAO).insert(any(Category.class));
    }

    @Test
    void createCategory_DuplicateName() {
        // 准备测试数据
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryDAO.findByNameAndUserId(anyString(), anyInt())).thenReturn(testCategory);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            categoryService.createCategory(
                testUser.getUserId(),
                testCategory.getName(),
                testCategory.getDescription()
            )
        );

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(categoryDAO).findByNameAndUserId(testCategory.getName(), testUser.getUserId());
        verify(categoryDAO, never()).insert(any(Category.class));
    }

    @Test
    void updateCategory_Success() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(testCategory);
        when(categoryDAO.update(any(Category.class))).thenReturn(1);

        // 执行测试
        Category result = categoryService.updateCategory(
            testCategory.getCategoryId(),
            "新分类名称",
            "新分类描述"
        );

        // 验证结果
        assertNotNull(result);
        assertEquals("新分类名称", result.getName());
        assertEquals("新分类描述", result.getDescription());

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
        verify(categoryDAO).update(any(Category.class));
    }

    @Test
    void updateCategory_CategoryNotFound() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            categoryService.updateCategory(
                testCategory.getCategoryId(),
                "新分类名称",
                "新分类描述"
            )
        );

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
        verify(categoryDAO, never()).update(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(testCategory);
        when(taskDAO.deleteByCategoryId(anyInt())).thenReturn(1);
        when(categoryDAO.delete(anyInt())).thenReturn(1);

        // 执行测试
        boolean result = categoryService.deleteCategory(testCategory.getCategoryId());

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
        verify(taskDAO).deleteByCategoryId(testCategory.getCategoryId());
        verify(categoryDAO).delete(testCategory.getCategoryId());
    }

    @Test
    void deleteCategory_CategoryNotFound() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            categoryService.deleteCategory(testCategory.getCategoryId())
        );

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
        verify(taskDAO, never()).deleteByCategoryId(anyInt());
        verify(categoryDAO, never()).delete(anyInt());
    }

    @Test
    void getCategory_Success() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(testCategory);

        // 执行测试
        Category result = categoryService.getCategory(testCategory.getCategoryId());

        // 验证结果
        assertNotNull(result);
        assertEquals(testCategory.getCategoryId(), result.getCategoryId());
        assertEquals(testCategory.getName(), result.getName());

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
    }

    @Test
    void getCategory_CategoryNotFound() {
        // 准备测试数据
        when(categoryDAO.findById(anyInt())).thenReturn(null);

        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () ->
            categoryService.getCategory(testCategory.getCategoryId())
        );

        // 验证方法调用
        verify(categoryDAO).findById(testCategory.getCategoryId());
    }

    @Test
    void getCategoriesByUser_Success() {
        // 准备测试数据
        List<Category> expectedCategories = Arrays.asList(testCategory);
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(categoryDAO.findByUserId(anyInt())).thenReturn(expectedCategories);

        // 执行测试
        List<Category> result = categoryService.getCategoriesByUser(testUser.getUserId());

        // 验证结果
        assertNotNull(result);
        assertEquals(expectedCategories.size(), result.size());
        assertEquals(testCategory.getCategoryId(), result.get(0).getCategoryId());

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(categoryDAO).findByUserId(testUser.getUserId());
    }

    @Test
    void deleteCategoriesByUser_Success() {
        // 准备测试数据
        when(userService.getUserById(anyInt())).thenReturn(testUser);
        when(taskDAO.deleteByUserId(anyInt())).thenReturn(1);
        when(categoryDAO.deleteByUserId(anyInt())).thenReturn(1);

        // 执行测试
        boolean result = categoryService.deleteCategoriesByUser(testUser.getUserId());

        // 验证结果
        assertTrue(result);

        // 验证方法调用
        verify(userService).getUserById(testUser.getUserId());
        verify(taskDAO).deleteByUserId(testUser.getUserId());
        verify(categoryDAO).deleteByUserId(testUser.getUserId());
    }
} 