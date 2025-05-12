package com.taskmanagement.dao.impl;

import com.taskmanagement.dao.CategoryDAO;
import com.taskmanagement.model.Category;
import com.taskmanagement.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务分类数据访问实现类
 */
public class CategoryDAOImpl extends BaseDAOImpl<Category, Integer> implements CategoryDAO {

    @Override
    protected Category convertToEntity(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setUserId(rs.getInt("user_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setCreatedDate(rs.getTimestamp("created_date"));
        return category;
    }

    @Override
    protected String getTableName() {
        return "categories";
    }

    @Override
    protected String getPrimaryKeyColumn() {
        return "category_id";
    }

    @Override
    protected Object[] getEntityValues(Category entity) {
        return new Object[]{
            entity.getUserId(),
            entity.getCategoryName(),
            entity.getDescription(),
            entity.getCreatedDate()
        };
    }

    @Override
    protected String[] getEntityColumns() {
        return new String[]{
            "user_id",
            "category_name",
            "description",
            "created_date"
        };
    }

    @Override
    public List<Category> findByUserId(Integer userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE user_id = ? ORDER BY created_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Category> categories = new ArrayList<>();
                while (rs.next()) {
                    categories.add(convertToEntity(rs));
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户分类列表失败", e);
        }
    }

    @Override
    public Category findByNameAndUserId(String categoryName, Integer userId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE category_name = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoryName);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return convertToEntity(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("根据名称和用户ID查询分类失败", e);
        }
    }

    @Override
    public int deleteByUserId(Integer userId) {
        String sql = "DELETE FROM " + getTableName() + " WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除用户分类失败", e);
        }
    }

    @Override
    public int updateName(Integer categoryId, String newName) {
        String sql = "UPDATE " + getTableName() + " SET category_name = ? WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newName);
            pstmt.setInt(2, categoryId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新分类名称失败", e);
        }
    }

    @Override
    public int updateDescription(Integer categoryId, String newDescription) {
        String sql = "UPDATE " + getTableName() + " SET description = ? WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newDescription);
            pstmt.setInt(2, categoryId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新分类描述失败", e);
        }
    }

    @Override
    public int insert(Category entity) {
        // 设置创建时间为当前时间
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        }
        return super.insert(entity);
    }
} 
 