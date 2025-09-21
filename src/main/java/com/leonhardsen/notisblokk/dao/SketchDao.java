package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Sketch;
import com.leonhardsen.notisblokk.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SketchDao extends ConnectionFactory {

    public void update(Sketch sketch) {
        String sql = "UPDATE RASCUNHOS SET RASCUNHO = ? WHERE ROWID = 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, sketch.getRascunho());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Sketch setSketch() {
        Sketch sketch = new Sketch();
        String sql = "SELECT RASCUNHO FROM RASCUNHOS WHERE ROWID = 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                sketch.setRascunho(rs.getBytes("RASCUNHO"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sketch;
    }
}