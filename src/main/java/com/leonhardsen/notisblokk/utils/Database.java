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
            String tabelaUsuarios = """                   
                    CREATE TABLE IF NOT EXISTS USUARIOS (
                        ID      INTEGER   PRIMARY KEY AUTOINCREMENT,
                        USUARIO TEXT      NOT NULL
                                          UNIQUE ON CONFLICT ROLLBACK,
                        SENHA   TEXT (64) NOT NULL
                                          DEFAULT (12345)
                    );
                    """;
            String tabelaTags = """                    
                    CREATE TABLE IF NOT EXISTS TAGS (
                        id  INTEGER PRIMARY KEY AUTOINCREMENT,
                        tag TEXT    UNIQUE
                                    NOT NULL
                    );
                    """;
            String tabelaNotas = """
                    CREATE TABLE IF NOT EXISTS NOTAS (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_tag INTEGER REFERENCES TAGS (id) ON DELETE CASCADE
                                                            ON UPDATE CASCADE
                                       NOT NULL ON CONFLICT ROLLBACK,
                        data   TEXT    NOT NULL,
                        titulo TEXT    NOT NULL,
                        relato BLOB,
                        status TEXT    NOT NULL ON CONFLICT ROLLBACK
                                       DEFAULT [A RESOLVER]
                    );
                    """;
            String tabelaStatus = """
                    CREATE TABLE IF NOT EXISTS STATUS (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        status TEXT    UNIQUE
                                       NOT NULL,
                        cor    TEXT    UNIQUE
                                       NOT NULL
                    );
                    """;
            String tabelaRascunhos = """
                    CREATE TABLE IF NOT EXISTS RASCUNHOS (
                        rascunho TEXT
                    );
                    """;
            String tabelaContatos = """
                    CREATE TABLE IF NOT EXISTS CONTATOS (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT
                                            NOT NULL,
                        nome        TEXT    UNIQUE
                                            NOT NULL,
                        telefone    TEXT,
                        email       TEXT,
                        endereco    TEXT,
                        observacoes TEXT
                    );
                  """;
            String insertRascunhos = """
                    INSERT INTO RASCUNHOS (rascunho)
                    SELECT 'Escreva algo aqui.'
                    WHERE NOT EXISTS (SELECT 1 FROM RASCUNHOS);
                """;
            stmt.execute(tabelaUsuarios);
            System.out.println("Tabela USUARIOS criada com sucesso.");
            stmt.execute(tabelaTags);
            System.out.println("Tabela TAGS criada com sucesso.");
            stmt.execute(tabelaNotas);
            System.out.println("Tabela NOTAS criada com sucesso.");
            stmt.execute(tabelaStatus);
            System.out.println("Tabela STATUS criada com sucesso.");
            stmt.execute(tabelaRascunhos);
            System.out.println("Tabela RASCUNHOS criada com sucesso.");
            stmt.execute(tabelaContatos);
            System.out.println("Tabela CONTATOS criada com sucesso.");
            stmt.execute(insertRascunhos);
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
