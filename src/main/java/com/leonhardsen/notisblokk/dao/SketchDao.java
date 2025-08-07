package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Sketch;
import com.leonhardsen.notisblokk.utils.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SketchDao extends ConnectionFactory {

    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    public SketchDao() {
        this.conn = getConnection();
    }

    public void update(Sketch sketch) {
        try {
            pstmt = conn.prepareStatement("UPDATE RASCUNHOS SET RASCUNHO = ? WHERE ROWID = 1") ;
            pstmt.setBytes(1, sketch.getRascunho());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    public Sketch setSketch() {
        Sketch sketch = new Sketch();
        try {
            pstmt = conn.prepareStatement("SELECT RASCUNHO FROM RASCUNHOS WHERE ROWID = 1");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                sketch.setRascunho(rs.getBytes("RASCUNHO"));
            }
            return sketch;
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

}
