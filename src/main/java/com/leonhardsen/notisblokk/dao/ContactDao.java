package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDao extends GenericDAO<Contact> {

    @Override
    public void save(Contact contact) {
        String sql = "INSERT INTO CONTATOS (NOME, TELEFONE, EMAIL, ENDERECO, OBSERVACOES) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getNome());
            pstmt.setString(2, contact.getTelefone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getEndereco());
            pstmt.setString(5, contact.getObservacoes());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Contact contact) {
        String sql = "UPDATE CONTATOS SET NOME = ?, TELEFONE = ?, EMAIL = ?, ENDERECO = ?, OBSERVACOES = ? WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getNome());
            pstmt.setString(2, contact.getTelefone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getEndereco());
            pstmt.setString(5, contact.getObservacoes());
            pstmt.setInt(6, contact.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Contact contact) {
        String sql = "DELETE FROM CONTATOS WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contact.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Contact> getAll() {
        return getAll(null); // Chama a pesquisa com string vazia para retornar todos
    }

    public ObservableList<Contact> getAll(String string) {
        List<Contact> listContacts = new ArrayList<>();
        String sql;
        if (string == null || string.isBlank()) {
            sql = "SELECT * FROM CONTATOS ORDER BY NOME";
        } else {
            sql = "SELECT * FROM CONTATOS WHERE NOME LIKE ? OR TELEFONE LIKE ? OR EMAIL LIKE ? OR ENDERECO LIKE ? OR OBSERVACOES LIKE ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (string != null && !string.isBlank()) {
                String likePattern = "%" + string + "%";
                pstmt.setString(1, likePattern);
                pstmt.setString(2, likePattern);
                pstmt.setString(3, likePattern);
                pstmt.setString(4, likePattern);
                pstmt.setString(5, likePattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Contact contact = new Contact();
                    contact.setId(rs.getInt("ID"));
                    contact.setNome(rs.getString("NOME"));
                    contact.setTelefone(rs.getString("TELEFONE"));
                    contact.setEmail(rs.getString("EMAIL"));
                    contact.setEndereco(rs.getString("ENDERECO"));
                    contact.setObservacoes(rs.getString("OBSERVACOES"));
                    listContacts.add(contact);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return FXCollections.observableArrayList(listContacts);
    }

    public Boolean findContact(String name) {
        String sql = "SELECT 1 FROM CONTATOS WHERE NOME = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}