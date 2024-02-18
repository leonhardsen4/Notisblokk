package com.leonhardsen.notisblokk.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    static Connection conn;
    static Statement stmt;

    public static void createTables() {
        conn = ConnectionFactory.getConnection();
        try {
            stmt = conn.createStatement();
            stmt.execute("""
                                    
                    --TABELA USUARIO--
                    CREATE TABLE IF NOT EXISTS USUARIOS (
                        ID        INTEGER   PRIMARY KEY AUTOINCREMENT,
                        USUARIO   TEXT      NOT NULL
                                            UNIQUE ON CONFLICT ROLLBACK,
                        SENHA     TEXT (64) NOT NULL
                                            DEFAULT (12345)
                    );
                    
                    --TABELA TAGS--
                    CREATE TABLE IF NOT EXISTS TAGS (
                        id  INTEGER PRIMARY KEY AUTOINCREMENT,
                        tag TEXT    UNIQUE
                                    NOT NULL
                    );
                    
                    --TABELA NOTAS--
                    CREATE TABLE IF NOT EXISTS NOTAS (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_tag INTEGER REFERENCES TAGS (id) ON DELETE CASCADE
                                                            ON UPDATE CASCADE
                                       NOT NULL ON CONFLICT ROLLBACK,
                        data   TEXT    NOT NULL,
                        titulo TEXT    NOT NULL,
                        relato TEXT,
                        status TEXT    NOT NULL ON CONFLICT ROLLBACK
                                       DEFAULT [A RESOLVER]
                    );
                                                                  
                    """);
            stmt.close();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e);
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

}
