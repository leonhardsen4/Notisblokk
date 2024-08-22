package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO extends GenericDAO<Users> {

    public UsersDAO() {
        this.conn = getConnection();
    }

    @Override
    public void save(Users usr) {
        try {
            sql = "INSERT INTO USUARIOS VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, usr.getUser());
            pstmt.setString(3, usr.getPassword());
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
    public void update(Users usr) {
        try {
            sql = "UPDATE USUARIOS SET " +
                    "USUARIO = ?, " +
                    "SENHA = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usr.getUser());
            pstmt.setString(2, usr.getPassword());
            pstmt.setInt(3, usr.getId());
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
    public void delete(Users usr) {
        try {
            sql = "DELETE FROM USUARIOS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usr.getId());
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
    public ObservableList<Users> getAll() {
        List<Users> usuarios = new ArrayList<>();
        try {
            sql = "SELECT * FROM USUARIOS";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Users usuario = new Users();
                usuario.setId(rs.getInt("ID"));
                usuario.setUser(rs.getString("USUARIO"));
                usuario.setPassword(rs.getString("SENHA"));
                usuarios.add(usuario);
            }
            return FXCollections.observableArrayList(usuarios);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    public Users find(String usr, String pass) {
        Users usuario = null;
        try {
            sql = "SELECT * FROM USUARIOS WHERE USUARIO LIKE '" + usr + "' AND SENHA LIKE '" + pass + "'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next()){
                usuario = new Users();
                usuario.setId(rs.getInt("ID"));
                usuario.setUser(rs.getString("USUARIO"));
                usuario.setPassword(rs.getString("SENHA"));
                closeConnection(conn, pstmt);
            }
        } catch (SQLException ex) {
            ex.fillInStackTrace();
            ex.getCause();
            throw new RuntimeException(ex.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
        return usuario;
    }

    public Boolean findUsername(String usr) {
        try {
            sql = "SELECT * FROM USUARIOS WHERE USUARIO LIKE '" + usr + "'";
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
