package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatusDAO extends GenericDAO<Status> {

    public StatusDAO() {
        this.conn = getConnection();
    }

    @Override
    public void save(Status status) {
        try {
            sql = "INSERT INTO STATUS VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, status.getStatus());
            pstmt.setString(3, status.getCor());
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
    public void update(Status status) {
        try {
            sql = "UPDATE STATUS SET " +
                    "STATUS = ?, " +
                    "COR = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.getStatus());
            pstmt.setString(2, status.getCor());
            pstmt.setInt(3, status.getId());
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
    public void delete(Status status) {
        try {
            sql = "DELETE FROM STATUS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, status.getId());
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
    public ObservableList<Status> getAll() {
        List<Status> status = new ArrayList<>();
        try {
            sql = "SELECT * FROM STATUS";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Status stt = new Status();
                stt.setId(rs.getInt("ID"));
                stt.setStatus(rs.getString("STATUS"));
                stt.setCor(rs.getString("COR"));
                status.add(stt);
            }
            return FXCollections.observableArrayList(status);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    public Status findByStatus(String statusName) {
        try {
            String sql = "SELECT * FROM status WHERE status = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Status status = new Status();
                status.setId(rs.getInt("id"));
                status.setStatus(rs.getString("status"));
                status.setCor(rs.getString("cor"));
                return status;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
        return null;
    }

}