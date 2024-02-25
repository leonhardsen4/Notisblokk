package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Notes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotesDAO extends GenericDAO<Notes> {

    public NotesDAO() {
        this.conn = getConnection();
    }

    public void save(Notes notes) {
        try {
            sql = "INSERT INTO NOTAS VALUES(?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(2, notes.getId_tag());
            pstmt.setString(3, notes.getData());
            pstmt.setString(4, notes.getTitulo());
            pstmt.setString(5, notes.getRelato());
            pstmt.setString(6, notes.getStatus());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    public void update(Notes notes) {
        try {
            sql = "UPDATE NOTAS SET " +
                    "ID_TAG = ?, " +
                    "DATA = ?, " +
                    "TITULO = ?, " +
                    "RELATO = ?, " +
                    "STATUS = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notes.getId_tag());
            pstmt.setString(2, notes.getData());
            pstmt.setString(3, notes.getTitulo());
            pstmt.setString(4, notes.getRelato());
            pstmt.setString(5, notes.getStatus());
            pstmt.setInt(6, notes.getId());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    public void delete(Notes notes) {
        try {
            sql = "DELETE FROM NOTAS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notes.getId());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    public ObservableList<Notes> getAll() {
        return null;
    }
    public ObservableList<Notes> getAll(int id_tag, String status) {
        List<Notes> notes = new ArrayList<>();
        try {
            if (status.equals("MOSTRAR TODOS")) {
                sql = "SELECT * FROM NOTAS WHERE ID_TAG = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id_tag);
            } else {
                sql = "SELECT * FROM NOTAS WHERE ID_TAG = ? AND STATUS LIKE ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id_tag);
                pstmt.setString(2, status);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Notes note = new Notes();
                note.setId(rs.getInt("ID"));
                note.setId_tag(rs.getInt("ID_TAG"));
                note.setData(rs.getString("DATA"));
                note.setTitulo(rs.getString("TITULO"));
                note.setRelato(rs.getString("RELATO"));
                note.setStatus(rs.getString("STATUS"));
                notes.add(note);
            }
            return FXCollections.observableArrayList(notes);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

}
