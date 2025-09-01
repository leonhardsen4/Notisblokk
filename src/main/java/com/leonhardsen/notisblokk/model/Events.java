package com.leonhardsen.notisblokk.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Events {
    private int id;
    private String nome;
    private LocalDate data;
    private LocalTime horarioInicial;
    private LocalTime horarioFinal;
    private String cor;
    private String observacoes;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Events() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getData() { return data; } // 🔥 AGORA DIRETO
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorarioInicial() { return horarioInicial; }
    public void setHorarioInicial(LocalTime horarioInicial) { this.horarioInicial = horarioInicial; }

    public LocalTime getHorarioFinal() { return horarioFinal; }
    public void setHorarioFinal(LocalTime horarioFinal) { this.horarioFinal = horarioFinal; }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getHorarioInicialAsString() {
        return horarioInicial != null ? horarioInicial.format(TIME_FORMATTER) : "";
    }

    public String getHorarioFinalAsString() {
        return horarioFinal != null ? horarioFinal.format(TIME_FORMATTER) : "";
    }

    public boolean conflitaCom(Events outroEvento) {
        if (this.data == null || outroEvento.getData() == null ||
                !this.data.equals(outroEvento.getData())) {
            return false;
        }

        return (this.horarioInicial.isBefore(outroEvento.getHorarioFinal()) &&
                this.horarioFinal.isAfter(outroEvento.getHorarioInicial()));
    }

    @Override
    public String toString() {
        return nome + " (" + getHorarioInicialAsString() + " - " + getHorarioFinalAsString() + ")";
    }
}