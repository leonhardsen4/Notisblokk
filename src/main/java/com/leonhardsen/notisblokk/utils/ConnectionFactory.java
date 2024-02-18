package com.leonhardsen.notisblokk.utils;

import java.sql.*;

public class ConnectionFactory {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:notisblokk.db";

    public static Connection getConnection() {
        Connection connection;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL);
            return connection;
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados", ex);
        }
    }

    public static void closeConnection(Connection connection, PreparedStatement statement){
        try {
            if (connection != null){
                connection.close();
            }

            if (statement != null){
                statement.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados", ex);
        }
    }

    public static void closeConnection(Connection connection, Statement statement){
        try {
            if (connection != null){
                connection.close();
            }

            if (statement != null){
                statement.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados", ex);
        }
    }

    public static void closeConnection(Connection connection, PreparedStatement statement, ResultSet resultSet){
        try {
            if (connection != null){
                connection.close();
            }

            if (statement != null){
                statement.close();
            }

            if (resultSet != null){
                resultSet.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro na conexão com o banco de dados", ex);
        }
    }
}

