package com.taskmanagement.dao.impl;

import com.taskmanagement.dao.BaseDAO;
import com.taskmanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础DAO实现类，提供通用的实现方法
 * @param <T> 实体类型
 * @param <K> 主键类型
 */
public abstract class BaseDAOImpl<T, K> implements BaseDAO<T, K> {
    
    /**
     * 将ResultSet转换为实体对象
     * @param rs ResultSet对象
     * @return 实体对象
     * @throws SQLException 如果转换过程中发生SQL异常
     */
    protected abstract T convertToEntity(ResultSet rs) throws SQLException;

    /**
     * 获取表名
     * @return 表名
     */
    protected abstract String getTableName();

    /**
     * 获取主键列名
     * @return 主键列名
     */
    protected abstract String getPrimaryKeyColumn();

    /**
     * 获取实体对象的属性值数组（用于插入和更新操作）
     * @param entity 实体对象
     * @return 属性值数组
     */
    protected abstract Object[] getEntityValues(T entity);

    /**
     * 获取实体对象的属性名数组（用于插入和更新操作）
     * @return 属性名数组
     */
    protected abstract String[] getEntityColumns();

    @Override
    public int insert(T entity) {
        String sql = buildInsertSQL();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            Object[] values = getEntityValues(entity);
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // 如果实体类有设置ID的方法，可以在这里设置
                        // 例如：((User)entity).setUserId(generatedKeys.getInt(1));
                    }
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException("插入数据失败", e);
        }
    }

    @Override
    public int deleteById(K id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除数据失败", e);
        }
    }

    @Override
    public int update(T entity) {
        String sql = buildUpdateSQL();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            Object[] values = getEntityValues(entity);
            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新数据失败", e);
        }
    }

    @Override
    public T findById(K id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertToEntity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败", e);
        }
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(convertToEntity(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败", e);
        }
    }

    @Override
    public List<T> findByCondition(String condition) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + condition;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(convertToEntity(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计记录数失败", e);
        }
    }

    /**
     * 构建插入SQL语句
     * @return 插入SQL语句
     */
    private String buildInsertSQL() {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        String[] columnNames = getEntityColumns();
        
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(columnNames[i]);
            values.append("?");
        }
        
        return "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + values + ")";
    }

    /**
     * 构建更新SQL语句
     * @return 更新SQL语句
     */
    private String buildUpdateSQL() {
        StringBuilder setClause = new StringBuilder();
        String[] columnNames = getEntityColumns();
        
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                setClause.append(", ");
            }
            setClause.append(columnNames[i]).append(" = ?");
        }
        
        return "UPDATE " + getTableName() + " SET " + setClause + " WHERE " + getPrimaryKeyColumn() + " = ?";
    }
} 