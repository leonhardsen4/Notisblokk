package com.leonhardsen.notisblokk.model;

public class Notes {

    private int id;
    private int id_tag;
    private String data;
    private String titulo;
    private byte[] relato;
    private String status;

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
}
