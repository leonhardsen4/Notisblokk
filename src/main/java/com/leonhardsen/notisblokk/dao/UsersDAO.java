package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO extends GenericDAO<Users> {

    @Override
    public void save(Users usr) {
        String sql = "INSERT INTO USUARIOS (USUARIO, SENHA) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usr.getUser());
            pstmt.setString(2, usr.getPassword());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Users usr) {
        String sql = "UPDATE USUARIOS SET USUARIO = ?, SENHA = ? WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usr.getUser());
            pstmt.setString(2, usr.getPassword());
            pstmt.setInt(3, usr.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Users usr) {
        String sql = "DELETE FROM USUARIOS WHERE ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usr.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Users> getAll() {
        List<Users> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIOS";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Users usuario = new Users();
                usuario.setId(rs.getInt("ID"));
                usuario.setUser(rs.getString("USUARIO"));
                usuario.setPassword(rs.getString("SENHA"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(usuarios);
    }

    public Users find(String usr, String pass) {
        Users usuario = null;
        String sql = "SELECT * FROM USUARIOS WHERE USUARIO = ? AND SENHA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usr);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    usuario = new Users();
                    usuario.setId(rs.getInt("ID"));
                    usuario.setUser(rs.getString("USUARIO"));
                    usuario.setPassword(rs.getString("SENHA"));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return usuario;
    }

    public Boolean findUsername(String usr) {
        String sql = "SELECT 1 FROM USUARIOS WHERE USUARIO = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usr);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}