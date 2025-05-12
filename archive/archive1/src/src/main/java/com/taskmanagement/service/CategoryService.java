package com.taskmanagement.service;

import com.taskmanagement.model.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {
    /**
     * 创建新分类
     * @param userId 用户ID
     * @param name 分类名称
     * @param description 分类描述
     * @return 创建的分类对象
     * @throws IllegalArgumentException 如果用户不存在或分类名称已存在
     */
    Category createCategory(Integer userId, String name, String description);

    /**
     * 更新分类信息
     * @param categoryId 分类ID
     * @param name 新分类名称
     * @param description 新分类描述
     * @return 更新后的分类对象
     * @throws IllegalArgumentException 如果分类不存在或新名称已被使用
     */
    Category updateCategory(Integer categoryId, String name, String description);

    /**
     * 删除分类
     * @param categoryId 分类ID
     * @return 是否删除成功
     * @throws IllegalArgumentException 如果分类不存在
     */
    boolean deleteCategory(Integer categoryId);

    /**
     * 获取分类信息
     * @param categoryId 分类ID
     * @return 分类对象
     * @throws IllegalArgumentException 如果分类不存在
     */
    Category getCategory(Integer categoryId);

    /**
     * 获取用户的所有分类
     * @param userId 用户ID
     * @return 分类列表
     * @throws IllegalArgumentException 如果用户不存在
     */
    List<Category> getUserCategories(Integer userId);

    /**
     * 根据名称查找分类
     * @param userId 用户ID
     * @param name 分类名称
     * @return 分类对象，如果不存在返回null
     */
    Category findCategoryByName(Integer userId, String name);

    /**
     * 验证分类名称是否可用
     * @param userId 用户ID
     * @param name 分类名称
     * @return 是否可用
     */
    boolean isCategoryNameAvailable(Integer userId, String name);

    /**
     * 删除用户的所有分类
     * @param userId 用户ID
     * @return 删除的分类数量
     * @throws IllegalArgumentException 如果用户不存在
     */
    int deleteUserCategories(Integer userId);
} 