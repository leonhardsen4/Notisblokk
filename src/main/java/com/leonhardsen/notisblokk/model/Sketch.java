package com.leonhardsen.notisblokk.model;

public class Sketch {

    private int id;
    private byte[] rascunho;

    public Sketch() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getRascunho() {
        return rascunho;
    }

    public void setRascunho(byte[] rascunho) {
        this.rascunho = rascunho;
    }
}
