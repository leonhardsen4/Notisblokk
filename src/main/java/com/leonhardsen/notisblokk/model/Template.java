package com.leonhardsen.notisblokk.model;

public class Template {

    private int id;
    private String titulo;
    private byte[] texto;

    public Template() {
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

    public byte[] getTexto() {
        return texto;
    }

    public void setTexto(byte[] texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return titulo;
    }

}
