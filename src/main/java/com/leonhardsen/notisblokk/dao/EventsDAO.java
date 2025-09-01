package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Events;
import com.leonhardsen.notisblokk.utils.ConnectionFactory;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventsDAO {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Events> searchEvents(String searchTerm) {
        List<Events> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTOS WHERE nome LIKE ? OR data LIKE ? OR horario_inicial LIKE ? OR observacoes LIKE ? ORDER BY data, horario_inicial ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likePattern = "%" + searchTerm + "%";
            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            stmt.setString(3, likePattern);
            stmt.setString(4, likePattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(resultSetToEvents(rs));
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return events;
    }

    public List<Events> findByDate(LocalDate data) {
        List<Events> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTOS WHERE data = ? ORDER BY horario_inicial";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data.format(DATE_FORMATTER));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(resultSetToEvents(rs));
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return events;
    }

    public int save(Events evento) {
        String sql = "INSERT INTO EVENTOS (data, nome, horario_inicial, horario_final, cor, observacoes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getData().format(DATE_FORMATTER));
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getHorarioInicialAsString());
            stmt.setString(4, evento.getHorarioFinalAsString());
            stmt.setString(5, evento.getCor());
            stmt.setString(6, evento.getObservacoes());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return -1;
    }

    public void update(Events evento) {
        String sql = "UPDATE EVENTOS SET data = ?, nome = ?, horario_inicial = ?, horario_final = ?, cor = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getData().format(DATE_FORMATTER));
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getHorarioInicialAsString());
            stmt.setString(4, evento.getHorarioFinalAsString());
            stmt.setString(5, evento.getCor());
            stmt.setString(6, evento.getObservacoes());
            stmt.setInt(7, evento.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM EVENTOS WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    private Events resultSetToEvents(ResultSet rs) throws SQLException {
        Events evento = new Events();
        evento.setId(rs.getInt("id"));

        String dataStr = rs.getString("data");
        evento.setData(LocalDate.parse(dataStr, DATE_FORMATTER));

        evento.setNome(rs.getString("nome"));

        evento.setHorarioInicial(java.time.LocalTime.parse(rs.getString("horario_inicial")));
        evento.setHorarioFinal(java.time.LocalTime.parse(rs.getString("horario_final")));
        evento.setCor(rs.getString("cor"));
        evento.setObservacoes(rs.getString("observacoes"));

        return evento;
    }
}