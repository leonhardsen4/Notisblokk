package com.leonhardsen.notisblokk.dao;

import com.leonhardsen.notisblokk.model.Audiences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AudiencesDAO extends GenericDAO<Audiences>{

    public AudiencesDAO() {
        this.conn = getConnection();
    }
    @Override
    public void save(Audiences audiences) {
        try {
            sql = "INSERT INTO AUDIENCIAS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(2, audiences.getProcesso());
            pstmt.setString(3, audiences.getJuiz());
            pstmt.setString(4, audiences.getPromotor());
            pstmt.setString(5, audiences.getData());
            pstmt.setString(6, audiences.getHorario());
            pstmt.setString(7, audiences.getTipo());
            pstmt.setString(8, audiences.getSituacao());
            pstmt.setString(9, audiences.getCapitulacao());
            pstmt.setString(10, audiences.getCompetencia());
            pstmt.setString(11, audiences.getDenuncia());
            pstmt.setString(12, audiences.getResposta());
            pstmt.setString(13, audiences.getLaudo());
            pstmt.setString(14, audiences.getPublicacao());
            pstmt.setString(15, audiences.getAntecedentes());
            pstmt.setString(16, audiences.getDistribuidor());
            pstmt.setString(17, audiences.getCotamp());
            pstmt.setString(18, audiences.getObservacoes());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public void update(Audiences audiences) {
        try {
            sql = "UPDATE AUDIENCIAS SET " +
                    "PROCESSO = ?, " +
                    "JUIZ = ?, " +
                    "PROMOTOR = ?, " +
                    "DATA = ?, " +
                    "HORARIO = ?, " +
                    "TIPO = ?, " +
                    "SITUACAO = ?, " +
                    "CAPITULACAO = ?, " +
                    "COMPETENCIA = ?, " +
                    "DENUNCIA = ?, " +
                    "RESPOSTA = ?, " +
                    "LAUDO = ?, " +
                    "PUBLICACAO = ?, " +
                    "ANTECEDENTES = ?, " +
                    "DISTRIBUIDOR = ?, " +
                    "COTAMP = ?, " +
                    "OBSERVACOES = ? " +
                    "WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, audiences.getProcesso());
            pstmt.setString(2, audiences.getJuiz());
            pstmt.setString(3, audiences.getPromotor());
            pstmt.setString(4, audiences.getData());
            pstmt.setString(5, audiences.getHorario());
            pstmt.setString(6, audiences.getTipo());
            pstmt.setString(7, audiences.getSituacao());
            pstmt.setString(8, audiences.getCapitulacao());
            pstmt.setString(9, audiences.getCompetencia());
            pstmt.setString(10, audiences.getDenuncia());
            pstmt.setString(11, audiences.getResposta());
            pstmt.setString(12, audiences.getLaudo());
            pstmt.setString(13, audiences.getPublicacao());
            pstmt.setString(14, audiences.getAntecedentes());
            pstmt.setString(15, audiences.getDistribuidor());
            pstmt.setString(16, audiences.getCotamp());
            pstmt.setString(17, audiences.getObservacoes());
            pstmt.setInt(18, audiences.getId());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public void delete(Audiences audiences) {
        try {
            sql = "DELETE FROM AUDIENCIAS WHERE ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, audiences.getId());
            pstmt.execute();
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt);
        }
    }

    @Override
    public ObservableList<Audiences> getAll() {
        List<Audiences> audiences = new ArrayList<>();
        try {
            sql = "SELECT * FROM AUDIENCES;";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Audiences audience = new Audiences();
                audience.setId(rs.getInt("ID"));
                audience.setProcesso(rs.getString("PROCESSO"));
                audience.setJuiz(rs.getString("JUIZ"));
                audience.setPromotor(rs.getString("PROMOTOR"));
                audience.setData(rs.getString("DATA"));
                audience.setHorario(rs.getString("HORARIO"));
                audience.setTipo(rs.getString("TIPO"));
                audience.setSituacao(rs.getString("SITUACAO"));
                audience.setCapitulacao(rs.getString("CAPITULACAO"));
                audience.setCompetencia(rs.getString("COMPETENCIA"));
                audience.setDenuncia(rs.getString("DENUNCIA"));
                audience.setResposta(rs.getString("RESPOSTA"));
                audience.setLaudo(rs.getString("LAUDO"));
                audience.setPublicacao(rs.getString("PUBLICACAO"));
                audience.setAntecedentes(rs.getString("ANTECEDENTES"));
                audience.setDistribuidor(rs.getString("DISTRIBUIDOR"));
                audience.setCotamp(rs.getString("COTAMP"));
                audience.setObservacoes(rs.getString("OBSERVACOES"));
                audiences.add(audience);
            }
            return FXCollections.observableArrayList(audiences);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    public ObservableList<Audiences> search(String string) {
        List<Audiences> audiences = new ArrayList<>();
        try {
            sql = "SELECT * FROM AUDIENCES WHERE " +
                    "DATA LIKE ? OR " +
                    "HORARIO LIKE ? OR " +
                    "PROCESSO LIKE ? OR " +
                    "JUIZ LIKE ? OR " +
                    "TIPO LIKE ?";
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, "%" + string + "%");
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Audiences audience = new Audiences();
                audience.setId(rs.getInt("ID"));
                audience.setProcesso(rs.getString("PROCESSO"));
                audience.setJuiz(rs.getString("JUIZ"));
                audience.setPromotor(rs.getString("PROMOTOR"));
                audience.setData(rs.getString("DATA"));
                audience.setHorario(rs.getString("HORARIO"));
                audience.setTipo(rs.getString("TIPO"));
                audience.setSituacao(rs.getString("SITUACAO"));
                audience.setCapitulacao(rs.getString("CAPITULACAO"));
                audience.setCompetencia(rs.getString("COMPETENCIA"));
                audience.setDenuncia(rs.getString("DENUNCIA"));
                audience.setResposta(rs.getString("RESPOSTA"));
                audience.setLaudo(rs.getString("LAUDO"));
                audience.setPublicacao(rs.getString("PUBLICACAO"));
                audience.setAntecedentes(rs.getString("ANTECEDENTES"));
                audience.setDistribuidor(rs.getString("DISTRIBUIDOR"));
                audience.setCotamp(rs.getString("COTAMP"));
                audience.setObservacoes(rs.getString("OBSERVACOES"));
                audiences.add(audience);
            }
            return FXCollections.observableArrayList(audiences);
        } catch (SQLException e) {
            e.fillInStackTrace();
            e.getCause();
            throw new RuntimeException(e.getMessage());
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

}
