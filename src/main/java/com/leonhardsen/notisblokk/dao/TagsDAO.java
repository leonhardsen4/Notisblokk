package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Tags;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagsDAO extends GenericDAO<Tags> {

    @Override
    public void save(Tags tags) {
        String sql = "INSERT INTO TAGS (tag) VALUES(?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tags.getTag());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Tags tags) {
        String sql = "UPDATE TAGS SET TAG = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tags.getTag());
            pstmt.setInt(2, tags.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Tags tags) {
        String sql = "DELETE FROM TAGS WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tags.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Tags> getAll() {
        List<Tags> tags = new ArrayList<>();
        String sql = "SELECT * FROM TAGS ORDER BY tag";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Tags tag = new Tags();
                tag.setId(rs.getInt("ID"));
                tag.setTag(rs.getString("TAG"));
                tags.add(tag);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(tags);
    }

    public ObservableList<Tags> search(String string) {
        List<Tags> tags = new ArrayList<>();
        String sql = "SELECT * FROM TAGS WHERE TAG LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + string + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tags tag = new Tags();
                    tag.setId(rs.getInt("ID"));
                    tag.setTag(rs.getString("TAG"));
                    tags.add(tag);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(tags);
    }

    public Tags findID(int id) {
        Tags tag = null;
        String sql = "SELECT * FROM TAGS WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    tag = new Tags();
                    tag.setId(rs.getInt("ID"));
                    tag.setTag(rs.getString("TAG"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

    public boolean findTag(String tagName) {
        String sql = "SELECT 1 FROM TAGS WHERE TAG = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tagName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}