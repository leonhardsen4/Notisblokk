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

            // Tabela de STATUS primeiro (para referência nas outras tabelas)
            String tabelaStatus = """
                    CREATE TABLE IF NOT EXISTS STATUS (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        status TEXT    UNIQUE NOT NULL,
                        cor    TEXT    UNIQUE NOT NULL
                    );
                    """;

            // Tabela de USUARIOS
            String tabelaUsuarios = """                   
                    CREATE TABLE IF NOT EXISTS USUARIOS (
                        ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                        USUARIO TEXT    NOT NULL UNIQUE,
                        SENHA   TEXT    NOT NULL DEFAULT '12345'
                    );
                    """;

            // Tabela de TAGS
            String tabelaTags = """                    
                    CREATE TABLE IF NOT EXISTS TAGS (
                        id  INTEGER PRIMARY KEY AUTOINCREMENT,
                        tag TEXT    UNIQUE NOT NULL
                    );
                    """;

            // Tabela de NOTAS com relação correta com ETIQUETAS
            String tabelaNotas = """
                    CREATE TABLE IF NOT EXISTS NOTAS (
                        id     INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_tag INTEGER NOT NULL,
                        data   TEXT    NOT NULL,
                        titulo TEXT    NOT NULL,
                        relato BLOB,
                        status TEXT    NOT NULL DEFAULT 'A RESOLVER',
                        FOREIGN KEY (id_tag) REFERENCES TAGS (id)\s
                            ON DELETE CASCADE ON UPDATE CASCADE,
                        FOREIGN KEY (status) REFERENCES STATUS (status)\s
                            ON DELETE RESTRICT ON UPDATE CASCADE
                    );
                   \s""";

            // Tabela de RASCUNHOS com registro único
            String tabelaRascunhos = """
                    CREATE TABLE IF NOT EXISTS RASCUNHOS (
                        id        INTEGER PRIMARY KEY CHECK (id = 1),
                        rascunho  BLOB    DEFAULT NULL
                    );
                    """;

            // Tabela de CONTATOS
            String tabelaContatos = """
                    CREATE TABLE IF NOT EXISTS CONTATOS (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome        TEXT    UNIQUE NOT NULL,
                        telefone    TEXT,
                        email       TEXT,
                        endereco    TEXT,
                        observacoes TEXT
                    );
                    """;

            // Tabela de DOCUMENTOS com conteúdo como BLOB
            String tabelaDocumentos = """
                CREATE TABLE IF NOT EXISTS DOCUMENTOS (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    conteudo BLOB,  -- Alterado para BLOB
                    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
                    id_documento_pai INTEGER DEFAULT 0,
                    FOREIGN KEY (id_documento_pai) REFERENCES DOCUMENTOS (id)
                        ON DELETE CASCADE ON UPDATE CASCADE
                );
                """;

            // Tabela EVENTOS
            String tabelaEventos = """
                    CREATE TABLE IF NOT EXISTS EVENTOS (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        data TEXT NOT NULL, -- formato: dd/MM/yyyy
                        nome TEXT NOT NULL,
                        horario_inicial TEXT NOT NULL,
                        horario_final TEXT NOT NULL,
                        cor TEXT NOT NULL DEFAULT '#ffffff',
                        observacoes TEXT
                    );
                    """;

            // Inserir status padrão
            String insertStatusPadrao = """
                    INSERT OR IGNORE INTO STATUS (status, cor) VALUES
                    ('A RESOLVER', '#FF0000'),
                    ('EM ANDAMENTO', '#FFFF00'),
                    ('RESOLVIDO', '#00FF00');
                   """;

            // Inserir registro único na tabela RASCUNHOS
            String insertRascunhos = """
                    INSERT OR IGNORE INTO RASCUNHOS (id, rascunho)\s
                    VALUES (1, 'Escreva aqui o que quiser.');
                   \s""";

            // Inserir utilizador padrão
            String inserirUsuarioPadrao = """
                    INSERT OR IGNORE INTO USUARIOS (USUARIO, SENHA) VALUES ('ADMIN', '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5');
                    """;

            // Executar criação das tabelas na ordem correta
            stmt.execute(tabelaStatus);
            System.out.println("Tabela STATUS criada com sucesso.");

            stmt.execute(tabelaTags);
            System.out.println("Tabela TAGS criada com sucesso.");

            stmt.execute(tabelaNotas);
            System.out.println("Tabela NOTAS criada com sucesso.");

            stmt.execute(tabelaUsuarios);
            System.out.println("Tabela USUARIOS criada com sucesso.");

            stmt.execute(tabelaRascunhos);
            System.out.println("Tabela RASCUNHOS criada com sucesso.");

            stmt.execute(tabelaContatos);
            System.out.println("Tabela CONTATOS criada com sucesso.");

            stmt.execute(tabelaDocumentos);
            System.out.println("Tabela DOCUMENTOS criada com sucesso.");

            stmt.execute(tabelaEventos);
            System.out.println("Tabela EVENTOS criada com sucesso.");

            // Inserir dados iniciais
            stmt.execute(insertRascunhos);
            System.out.println("Registro único na tabela RASCUNHOS criado com sucesso.");

            stmt.execute(insertStatusPadrao);
            System.out.println("Status padrão inseridos com sucesso.");

            stmt.execute(inserirUsuarioPadrao);
            System.out.println("Usuário padrão inserido com sucesso.");

            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
            e.fillInStackTrace();
            throw new RuntimeException("Falha na criação do banco de dados", e);
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }
}