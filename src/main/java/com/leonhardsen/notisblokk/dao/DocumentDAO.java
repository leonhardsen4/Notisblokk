package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Document;
import com.leonhardsen.notisblokk.utils.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO extends ConnectionFactory {

    Connection conn;
    PreparedStatement pstmt;
    ResultSet rs;

    public DocumentDAO() {
        this.conn = getConnection();
    }

    public List<Document> buscarTodos() {
        List<Document> documentos = new ArrayList<>();
        String sql = "SELECT id, titulo, conteudo, tipo, numero_processo, data_criacao, data_documento, id_documento_pai " +
                "FROM DOCUMENTOS ORDER BY data_documento DESC, data_criacao DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(new Document(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getBytes("conteudo"),
                        rs.getTimestamp("data_criacao").toLocalDateTime().toLocalDate(),
                        rs.getInt("id_documento_pai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar documentos: " + e.getMessage());
        }

        return documentos;
    }

    public Document buscarPorId(int id) {
        String sql = "SELECT id, titulo, conteudo, data_criacao, id_documento_pai FROM DOCUMENTOS WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Document(
                        rs.getInt("id"), // Adicionado o ID
                        rs.getString("titulo"),
                        rs.getBytes("conteudo"),
                        rs.getTimestamp("data_criacao").toLocalDateTime().toLocalDate(),
                        rs.getInt("id_documento_pai")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar documento por ID: " + e.getMessage());
        }

        return null;
    }

    public boolean salvar(Document document) {
        String sql = "INSERT INTO DOCUMENTOS (titulo, conteudo, data_criacao, id_documento_pai) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP, ?)"; // Usar CURRENT_TIMESTAMP

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitulo());
            pstmt.setBytes(2, document.getConteudo());
            pstmt.setInt(3, document.getIdDocumentoPai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar documento: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(Document document) {
        String sql = "UPDATE DOCUMENTOS SET titulo = ?, conteudo = ?, data_criacao = ?, id_documento_pai = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, document.getTitulo());
            pstmt.setBytes(2, document.getConteudo()); // Alterado para setBytes
            pstmt.setDate(3, Date.valueOf(document.getDataCriacao()));
            pstmt.setInt(4, document.getIdDocumentoPai());
            pstmt.setInt(5, document.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar documento: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM DOCUMENTOS WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir documento: " + e.getMessage());
            return false;
        }
    }

    public List<Document> buscarPorTituloConteudo(String string) {
        List<Document> documentos = new ArrayList<>();
        String sql = "SELECT id, titulo, conteudo, data_criacao, id_documento_pai " +
                "FROM DOCUMENTOS WHERE titulo LIKE ? OR conteudo LIKE ? ORDER BY data_documento ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + string + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                documentos.add(new Document(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getBytes("conteudo"),
                        rs.getTimestamp("data_criacao").toLocalDateTime().toLocalDate(),
                        rs.getInt("id_documento_pai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar documentos por número de processo: " + e.getMessage());
        }

        return documentos;
    }

    public List<Document> buscarMetadados() {
        List<Document> documentos = new ArrayList<>();
        String sql = "SELECT id, titulo, data_criacao, id_documento_pai FROM DOCUMENTOS ORDER BY id ASC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getInt("id"));
                doc.setTitulo(rs.getString("titulo"));
                doc.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime().toLocalDate());
                doc.setIdDocumentoPai(rs.getInt("id_documento_pai"));

                documentos.add(doc);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar metadados dos documentos: " + e.getMessage());
        }

        return documentos;
    }

    public List<Document> buscarTodosComHierarquia() {
        List<Document> documentos = new ArrayList<>();
        String sql = "SELECT id, titulo, conteudo, data_criacao, id_documento_pai " +
                "FROM DOCUMENTOS ORDER BY id_documento_pai, data_criacao";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                documentos.add(new Document(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getBytes("conteudo"),
                        rs.getTimestamp("data_criacao").toLocalDateTime().toLocalDate(),
                        rs.getInt("id_documento_pai")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar documentos com hierarquia: " + e.getMessage());
        }

        return documentos;
    }

}
