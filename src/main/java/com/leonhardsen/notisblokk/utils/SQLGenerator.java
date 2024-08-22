package com.leonhardsen.notisblokk.utils;

import java.lang.reflect.Field;

public class SQLGenerator {

    public static String generateCreateTableSQL(Class<?> clazz) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + clazz.getSimpleName() + " (");

        // Obtém todos os atributos da classe
        Field[] fields = clazz.getDeclaredFields();

        // Verifica se a classe possui pelo menos um atributo
        if (fields.length == 0) {
            throw new IllegalArgumentException("A classe não possui atributos.");
        }

        // Obtém o primeiro atributo como chave primária e autoincrementável
        String firstFieldName = fields[0].getName();
        String primaryKeySQL = firstFieldName + " INTEGER PRIMARY KEY AUTOINCREMENT";

        // Adiciona a chave primária à instrução SQL
        sql.append(primaryKeySQL).append(",");

        // Itera sobre os atributos, começando pelo segundo (após a chave primária)
        for (int i = 1; i < fields.length; i++) {
            // Obtém o nome e o tipo do atributo
            String fieldName = fields[i].getName();
            Class<?> fieldType = fields[i].getType();

            // Converte o tipo do atributo para o tipo adequado do SQLite
            String columnType = getSQLiteType(fieldType);

            // Adiciona o campo à instrução SQL
            sql.append(fieldName).append(" ").append(columnType).append(",");
        }

        // Remove a última vírgula adicionada
        sql.deleteCharAt(sql.length() - 1);

        // Fecha a instrução SQL
        sql.append(");");

        return sql.toString();
    }

    private static String getSQLiteType(Class<?> javaType) {
        if (javaType == int.class || javaType == Integer.class) {
            return "INTEGER";
        } else if (javaType == long.class || javaType == Long.class) {
            return "INTEGER";
        } else if (javaType == float.class || javaType == Float.class) {
            return "REAL";
        } else if (javaType == double.class || javaType == Double.class) {
            return "REAL";
        } else if (javaType == boolean.class || javaType == Boolean.class) {
            return "INTEGER"; // SQLite doesn't have a boolean type, using INTEGER instead
        } else if (javaType == String.class) {
            return "TEXT";
        } else {
            // Trate outros tipos conforme necessário
            return "TEXT"; // Por padrão, tratamos todos os outros tipos como texto
        }
    }
}
