package com.leonhardsen.notisblokk.utils;

import com.leonhardsen.notisblokk.dao.UsersDAO;
import com.leonhardsen.notisblokk.model.Users;

public class Testes {

    public static void main(String[] args) {

        Users usuario = new Users();
        Database.createTables();
        usuario.setUser("");
        usuario.setPassword(Crypthograph.SHA256("12345"));
        UsersDAO usersDAO = new UsersDAO();
        usersDAO.save(usuario);
    }
}
