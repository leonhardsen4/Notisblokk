package com.leonhardsen.notisblokk.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notes {

    private int id;
    private int id_tag;
    private String data;
    private String titulo;
    private byte[] relato;
    private String status;
    private LocalDateTime ultimaModificacao;
    private String deadline;

    public Notes() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_tag() {
        return id_tag;
    }

    public void setId_tag(int id_tag) {
        this.id_tag = id_tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public byte[] getRelato() {
        return relato;
    }

    public void setRelato(byte[] relato) {
        this.relato = relato;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(LocalDateTime ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getUltimaModificacaoFormatada() {
        if (ultimaModificacao != null) {
            return ultimaModificacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        return "";
    }

}
