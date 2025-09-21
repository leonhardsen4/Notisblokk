package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Template;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Template.
 */
public class TemplateDAO extends GenericDAO<Template> {

    @Override
    public void save(Template template) {
        String sql = "INSERT INTO TEMPLATES (titulo, texto) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, template.getTitulo());
            pstmt.setBytes(2, template.getTexto());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Template template) {
        String sql = "UPDATE TEMPLATES SET titulo = ?, texto = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, template.getTitulo());
            pstmt.setBytes(2, template.getTexto());
            pstmt.setInt(3, template.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Template template) {
        String sql = "DELETE FROM TEMPLATES WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, template.getId());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<Template> getAll() {
        List<Template> templates = new ArrayList<>();
        String sql = "SELECT * FROM TEMPLATES ORDER BY titulo";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Template template = new Template();
                template.setId(rs.getInt("id"));
                template.setTitulo(rs.getString("titulo"));
                template.setTexto(rs.getBytes("texto"));
                templates.add(template);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return FXCollections.observableArrayList(templates);
    }
}