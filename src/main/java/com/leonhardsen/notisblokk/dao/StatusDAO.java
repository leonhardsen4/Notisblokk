package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatusDAO extends GenericDAO<Status> {

    @Override
    public void save(Status status) {
        String sql = "INSERT INTO STATUS (status, cor) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.getStatus());
            pstmt.setString(2, status.getCor());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Status status) {
        String sql = "UPDATE STATUS SET STATUS = ?, COR = ? WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.getStatus());
            pstmt.setString(2, status.getCor());
            pstmt.setInt(3, status.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Status status) {
        String sql = "DELETE FROM STATUS WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Status> getAll() {
        List<Status> statusList = new ArrayList<>();
        String sql = "SELECT * FROM STATUS ORDER BY status";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Status stt = new Status();
                stt.setId(rs.getInt("ID"));
                stt.setStatus(rs.getString("STATUS"));
                stt.setCor(rs.getString("COR"));
                statusList.add(stt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(statusList);
    }

    public Status findByStatus(String statusName) {
        Status status = null;
        String sql = "SELECT * FROM STATUS WHERE STATUS = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, statusName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = new Status();
                    status.setId(rs.getInt("id"));
                    status.setStatus(rs.getString("status"));
                    status.setCor(rs.getString("cor"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return status;
    }

    public Boolean findStatus(String status, String cor) {
        String sql = "SELECT 1 FROM STATUS WHERE STATUS = ? OR COR = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, cor);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}