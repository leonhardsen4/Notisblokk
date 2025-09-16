package com.leonhardsen.notisblokk.model;

import java.time.LocalDate;
import java.util.Arrays;

public class Document {

    private int id;
    private String titulo;
    private byte[] conteudo;
    private LocalDate dataCriacao;
    private int idDocumentoPai;

    public Document() {
    }

    public Document(int id, String titulo, byte[] conteudo, LocalDate dataCriacao, int idDocumentoPai) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataCriacao = dataCriacao;
        this.idDocumentoPai = idDocumentoPai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public byte[] getConteudo() {
        return conteudo;
    }

    public void setConteudo(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getIdDocumentoPai() {
        return idDocumentoPai;
    }

    public void setIdDocumentoPai(int idDocumentoPai) {
        this.idDocumentoPai = idDocumentoPai;
    }

    // Obter conteúdo como String
    public String getConteudoAsString() {
        if (conteudo != null) {
            return new String(conteudo);
        }
        return "";
    }

    // Definir conteúdo como String
    public void setConteudoAsString(String conteudo) {
        if (conteudo != null) {
            this.conteudo = conteudo.getBytes();
        } else {
            this.conteudo = null;
        }
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", conteudo=" + Arrays.toString(conteudo) +
                ", dataCriacao=" + dataCriacao +
                ", idDocumentoPai=" + idDocumentoPai +
                '}';
    }

}
