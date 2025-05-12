package com.taskmanagement.service.impl;

import com.taskmanagement.dao.CategoryDAO;
import com.taskmanagement.dao.TaskDAO;
import com.taskmanagement.dao.impl.CategoryDAOImpl;
import com.taskmanagement.dao.impl.TaskDAOImpl;
import com.taskmanagement.model.Category;
import com.taskmanagement.service.CategoryService;
import com.taskmanagement.service.UserService;

import java.util.List;

/**
 * 分类服务实现类
 */
public class CategoryServiceImpl implements CategoryService {
    private final CategoryDAO categoryDAO;
    private final TaskDAO taskDAO;
    private final UserService userService;

    public CategoryServiceImpl() {
        this.categoryDAO = new CategoryDAOImpl();
        this.taskDAO = new TaskDAOImpl();
        this.userService = new UserServiceImpl();
    }

    @Override
    public Category createCategory(Integer userId, String name, String description) {
        // 验证用户是否存在
        userService.getUserById(userId);

        // 验证分类名称是否可用
        if (!isCategoryNameAvailable(userId, name)) {
            throw new IllegalArgumentException("分类名称已存在");
        }

        // 创建新分类
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setDescription(description);

        // 保存分类
        categoryDAO.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Integer categoryId, String name, String description) {
        // 获取现有分类
        Category category = getCategory(categoryId);

        // 如果修改了名称，需要验证新名称是否可用
        if (!category.getName().equals(name) && !isCategoryNameAvailable(category.getUserId(), name)) {
            throw new IllegalArgumentException("分类名称已被使用");
        }

        // 更新分类信息
        category.setName(name);
        category.setDescription(description);
        categoryDAO.update(category);
        return category;
    }

    @Override
    public boolean deleteCategory(Integer categoryId) {
        // 验证分类是否存在
        Category category = getCategory(categoryId);

        // 删除分类下的所有任务
        taskDAO.deleteByCategoryId(categoryId);

        // 删除分类
        return categoryDAO.delete(categoryId) > 0;
    }

    @Override
    public Category getCategory(Integer categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        return category;
    }

    @Override
    public List<Category> getUserCategories(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);
        return categoryDAO.findByUserId(userId);
    }

    @Override
    public Category findCategoryByName(Integer userId, String name) {
        return categoryDAO.findByNameAndUserId(name, userId);
    }

    @Override
    public boolean isCategoryNameAvailable(Integer userId, String name) {
        return findCategoryByName(userId, name) == null;
    }

    @Override
    public int deleteUserCategories(Integer userId) {
        // 验证用户是否存在
        userService.getUserById(userId);

        // 获取用户的所有分类
        List<Category> categories = getUserCategories(userId);

        // 删除每个分类下的任务
        for (Category category : categories) {
            taskDAO.deleteByCategoryId(category.getCategoryId());
        }

        // 删除所有分类
        return categoryDAO.deleteByUserId(userId);
    }
} 