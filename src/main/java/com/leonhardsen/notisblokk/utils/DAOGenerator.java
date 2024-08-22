package com.leonhardsen.notisblokk.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOGenerator<T> {

    private final Connection conn;

    public DAOGenerator() {
        this.conn = ConnectionFactory.getConnection();
    }

    public void save(T obj) throws SQLException, IllegalAccessException {
        String tableName = obj.getClass().getSimpleName();

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");

        StringBuilder placeholders = new StringBuilder();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldName.equals("id")) {
                sql.append(fieldName).append(", ");
                placeholders.append("?, ");
            }
        }
        sql.delete(sql.length() - 2, sql.length());
        placeholders.delete(placeholders.length() - 2, placeholders.length());
        sql.append(") VALUES (").append(placeholders).append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!fieldName.equals("id")) {
                    field.setAccessible(true);
                    pstmt.setObject(parameterIndex++, field.get(obj));
                }
            }
            pstmt.executeUpdate();
        }
    }

    public void update(T obj) throws SQLException, IllegalAccessException {
        String tableName = obj.getClass().getSimpleName();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldName.equals("id")) {
                sql.append(fieldName).append(" = ?, ");
            }
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" WHERE id = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!fieldName.equals("id")) {
                    field.setAccessible(true);
                    pstmt.setObject(parameterIndex++, field.get(obj));
                }
            }
            // Set ID
            for (Field field : fields) {
                if (field.getName().equals("id")) {
                    field.setAccessible(true);
                    pstmt.setObject(parameterIndex++, field.get(obj));
                    break;
                }
            }
            pstmt.executeUpdate();
        }
    }

    public void remove(T obj) throws SQLException {
        String tableName = obj.getClass().getSimpleName();
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            pstmt.setObject(1, idField.get(obj));
            pstmt.executeUpdate();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> listAll(Class<T> clazz) throws SQLException, IllegalAccessException, InstantiationException {
        String tableName = clazz.getSimpleName();
        String sql = "SELECT * FROM " + tableName;

        List<T> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            resultList(clazz, list, rs);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public T findByID(Class<T> clazz, int id) throws SQLException, IllegalAccessException, InstantiationException {
        String tableName = clazz.getSimpleName();
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    T obj = clazz.getDeclaredConstructor().newInstance();
                    for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        field.set(obj, rs.getObject(field.getName()));
                    }
                    return obj;
                }
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<T> search(Class<T> clazz, String searchString) throws SQLException, IllegalAccessException, InstantiationException {
        String tableName = clazz.getSimpleName();
        String sql = "SELECT * FROM " + tableName + " WHERE ...";

        List<T> list = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                resultList(clazz, list, rs);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    private void resultList(Class<T> clazz, List<T> list, ResultSet rs) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        while (rs.next()) {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(obj, rs.getObject(field.getName()));
            }
            list.add(obj);
        }
    }

}

