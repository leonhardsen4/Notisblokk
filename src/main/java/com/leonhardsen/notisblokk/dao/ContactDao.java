package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDao extends GenericDAO<Contact> {

    public ContactDao() {
        this.conn = getConnection();
    }

    @Override
    public void save(Contact contact) {
        try {
            sql = "INSERT INTO CONTATOS VALUES(?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, contact.getNome());
            pstmt.setString(3, contact.getTelefone());
            pstmt.setString(4, contact.getEmail());
            pstmt.setString(5, contact.getEndereco());
            pstmt.setString(6, contact.getObservacoes());
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
    public void update(Contact contact) {
        try {
            sql = "UPDATE CONTATOS SET " +
                    "NOME = ?, " +
                    "TELEFONE = ?, " +
                    "EMAIL = ?, " +
                    "ENDERECO = ?, " +
                    "OBSERVACOES = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, contact.getNome());
            pstmt.setString(2, contact.getTelefone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getEndereco());
            pstmt.setString(5, contact.getObservacoes());
            pstmt.setInt(6, contact.getId());
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
    public void delete(Contact contact) {
        try {
            sql = "DELETE FROM CONTATOS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, contact.getId());
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
    public ObservableList<Contact> getAll() {
        return null;
    }

    public ObservableList<Contact> getAll(String string) {
        List<Contact> listContacts = new ArrayList<>();
        try {
            if (string == null || string.isEmpty() || string.isBlank()) {
                sql = "SELECT * FROM CONTATOS ORDER BY NOME";
            } else {
                sql = "SELECT * FROM CONTATOS WHERE NOME LIKE '%" + string + "%' " +
                        "OR TELEFONE LIKE '%" + string + "%' " +
                        "OR EMAIL LIKE '%" + string + "%' " +
                        "OR ENDERECO LIKE '%" + string + "%' " +
                        "OR OBSERVACOES LIKE '%" + string + "%' ";
            }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
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
            return FXCollections.observableArrayList(listContacts);
        } catch (SQLException ex) {
            ex.fillInStackTrace();
            ex.getCause();
            throw new RuntimeException(ex.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

        public Boolean findContact(String string) {
            try {
                sql = "SELECT * FROM CONTATOS WHERE NOME LIKE '" + string + "'";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException ex) {
                ex.fillInStackTrace();
                ex.getCause();
                throw new RuntimeException(ex.getMessage());
            } finally {
                closeConnection(conn, pstmt, rs);
            }
        }
    }
