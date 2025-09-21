package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Notes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotesDAO extends GenericDAO<Notes> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void save(Notes notes) {
        String sql = "INSERT INTO NOTAS (id_tag, data, titulo, relato, status, ultima_modificacao, deadline) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notes.getId_tag());
            pstmt.setString(2, notes.getData());
            pstmt.setString(3, notes.getTitulo());
            pstmt.setBytes(4,notes.getRelato());
            pstmt.setString(5, notes.getStatus());
            pstmt.setString(6, LocalDateTime.now().format(formatter));
            pstmt.setString(7, notes.getDeadline());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Notes notes) {
        String sql = "UPDATE NOTAS SET ID_TAG = ?, DATA = ?, TITULO = ?, RELATO = ?, STATUS = ?, ULTIMA_MODIFICACAO = ?, DEADLINE = ? WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notes.getId_tag());
            pstmt.setString(2, notes.getData());
            pstmt.setString(3, notes.getTitulo());
            pstmt.setBytes(4,notes.getRelato());
            pstmt.setString(5, notes.getStatus());
            pstmt.setString(6, LocalDateTime.now().format(formatter));
            pstmt.setString(7, notes.getDeadline());
            pstmt.setInt(8, notes.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Notes notes) {
        String sql = "DELETE FROM NOTAS WHERE ID = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notes.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Notes> getAll() {
        List<Notes> notes = new ArrayList<>();
        String sql = "SELECT * FROM NOTAS";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                notes.add(extractNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(notes);
    }

    /**
     * Busca notas com base na tag, status e se está concluída.
     */
    public ObservableList<Notes> getAll(int id_tag, String status) {
        List<Notes> notes = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM NOTAS WHERE ID_TAG = ?");
        if (!"MOSTRAR TODOS".equals(status)) query.append(" AND STATUS = ?");

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, id_tag);
            if (!"MOSTRAR TODOS".equals(status)) {
                pstmt.setString(paramIndex, status);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(notes);
    }

    /**
     * Busca notas em todas as etiquetas pelo título ou relato.
     */
    public ObservableList<Notes> searchAllNotes(String searchText) {
        List<Notes> notes = new ArrayList<>();
        String sql = "SELECT * FROM NOTAS WHERE titulo LIKE ? OR relato LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likePattern = "%" + searchText + "%";
            pstmt.setString(1, likePattern);
            pstmt.setString(2, likePattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(notes);
    }

    /**
     * Busca notas globalmente por status e/ou estado de conclusão.
     * @param status O status a filtrar (pode ser null).
     * @param concluida O estado de conclusão a filtrar (pode ser null).
     * @return Uma lista de notas que correspondem aos filtros.
     */
    public ObservableList<Notes> buscarNotasGlobais(String status, Boolean concluida) {
        List<Notes> notes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM NOTAS WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.equals("MOSTRAR TODOS")) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (concluida != null) {
            sql.append(" AND concluida = ?");
            params.add(concluida);
        }
        sql.append(" ORDER BY data DESC, id DESC");

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(extractNoteFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(notes);
    }

    private Notes extractNoteFromResultSet(ResultSet rs) throws SQLException {
        Notes note = new Notes();
        note.setId(rs.getInt("ID"));
        note.setId_tag(rs.getInt("ID_TAG"));
        note.setData(rs.getString("DATA"));
        note.setTitulo(rs.getString("TITULO"));
        note.setRelato(rs.getBytes("RELATO"));
        note.setStatus(rs.getString("STATUS"));
        String ultimaModificacaoStr = rs.getString("ultima_modificacao");
        if (ultimaModificacaoStr != null) {
            note.setUltimaModificacao(LocalDateTime.parse(ultimaModificacaoStr, formatter));
        }
        note.setDeadline(rs.getString("deadline"));
        return note;
    }
}