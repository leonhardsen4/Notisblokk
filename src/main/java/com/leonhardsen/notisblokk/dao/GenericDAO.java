package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.utils.ConnectionFactory;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class GenericDAO<T> extends ConnectionFactory {

    Connection conn;
    String sql;
    PreparedStatement pstmt;
    ResultSet rs;

    public abstract void save(T t);

    public abstract void update(T t);

    public abstract void delete(T t);

    public abstract ObservableList<T> getAll();

}
