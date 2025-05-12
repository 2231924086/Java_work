package com.taskmanagement.dao;

import com.taskmanagement.model.Category;
import java.util.List;

/**
 * 任务分类数据访问接口
 */
public interface CategoryDAO extends BaseDAO<Category, Integer> {
    /**
     * 获取用户的所有分类
     * @param userId 用户ID
     * @return 分类列表
     */
    List<Category> findByUserId(Integer userId);

    /**
     * 根据分类名称和用户ID查找分类
     * @param categoryName 分类名称
     * @param userId 用户ID
     * @return 分类对象，如果不存在返回null
     */
    Category findByNameAndUserId(String categoryName, Integer userId);

    /**
     * 删除用户的所有分类
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(Integer userId);

    /**
     * 更新分类名称
     * @param categoryId 分类ID
     * @param newName 新名称
     * @return 影响的行数
     */
    int updateName(Integer categoryId, String newName);

    /**
     * 更新分类描述
     * @param categoryId 分类ID
     * @param newDescription 新描述
     * @return 影响的行数
     */
    int updateDescription(Integer categoryId, String newDescription);
} 