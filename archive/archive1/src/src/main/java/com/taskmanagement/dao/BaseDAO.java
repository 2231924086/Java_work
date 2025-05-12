package com.taskmanagement.dao;

import java.util.List;

/**
 * 基础DAO接口，定义通用的数据库操作方法
 * @param <T> 实体类型
 * @param <K> 主键类型
 */
public interface BaseDAO<T, K> {
    /**
     * 插入新记录
     * @param entity 实体对象
     * @return 影响的行数
     */
    int insert(T entity);

    /**
     * 根据主键删除记录
     * @param id 主键
     * @return 影响的行数
     */
    int deleteById(K id);

    /**
     * 更新记录
     * @param entity 实体对象
     * @return 影响的行数
     */
    int update(T entity);

    /**
     * 根据主键查询记录
     * @param id 主键
     * @return 实体对象
     */
    T findById(K id);

    /**
     * 查询所有记录
     * @return 实体对象列表
     */
    List<T> findAll();

    /**
     * 根据条件查询记录
     * @param condition 查询条件
     * @return 实体对象列表
     */
    List<T> findByCondition(String condition);

    /**
     * 统计记录总数
     * @return 记录总数
     */
    long count();
} 