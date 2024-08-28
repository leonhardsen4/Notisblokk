package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Tags;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagsDAO extends GenericDAO<Tags> {

    public TagsDAO() {
        this.conn = getConnection();
    }

    @Override
    public void save(Tags tags) {
        try {
            sql = "INSERT INTO TAGS VALUES(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, tags.getTag());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public void update(Tags tags) {
        try {
            sql = "UPDATE TAGS SET " +
                    "TAG = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tags.getTag());
            pstmt.setInt(2, tags.getId());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public void delete(Tags tags) {
        try {

            conn.setAutoCommit(false);

            String sqlDeleteNotes = "DELETE FROM NOTAS WHERE ID_TAG = ?";
            try (PreparedStatement pstmtNotes = conn.prepareStatement(sqlDeleteNotes)) {
                pstmtNotes.setInt(1, tags.getId());
                pstmtNotes.executeUpdate();
            }

            sql = "DELETE FROM TAGS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tags.getId());
            pstmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.fillInStackTrace();
            }
            e.fillInStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.fillInStackTrace();
            }
            closeConnection(conn, pstmt);
        }
    }


    @Override
    public ObservableList<Tags> getAll() {
        List<Tags> tags = new ArrayList<>();
        try {
            sql = "SELECT * FROM TAGS";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Tags tag = new Tags();
                tag.setId(rs.getInt("ID"));
                tag.setTag(rs.getString("TAG"));
                tags.add(tag);
            }
            return FXCollections.observableArrayList(tags);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    public ObservableList<Tags> search(String string) {
        List<Tags> tags = new ArrayList<>();
        try {
            sql = "SELECT * FROM TAGS " +
                    "WHERE TAG LIKE '%" + string + "%'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Tags tag = new Tags();
                tag.setId(rs.getInt("ID"));
                tag.setTag(rs.getString("TAG"));
                tags.add(tag);
            }
            return FXCollections.observableArrayList(tags);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    public Tags findID(int id) {
        Tags tag = new Tags();
        try {
            sql = "SELECT * FROM TAGS WHERE ID = '" + id + "'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tag.setId(rs.getInt("ID"));
                tag.setTag(rs.getString("TAG"));
            }
            return tag;
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

}
