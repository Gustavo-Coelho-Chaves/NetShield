package com.example.netshield_porta.model;

import java.util.List;

public class RelatorioSeguranca {

    private int score;
    private String classificacao;
    private List<Porta> portasCriticas;
    private int totalPortasAbertas;

    public RelatorioSeguranca(int score, String classificacao,
                              List<Porta> portasCriticas, int totalPortasAbertas) {
        this.score = score;
        this.classificacao = classificacao;
        this.portasCriticas = portasCriticas;
        this.totalPortasAbertas = totalPortasAbertas;
    }

    public int getScore()                  { return score; }
    public String getClassificacao()       { return classificacao; }
    public List<Porta> getPortasCriticas() { return portasCriticas; }
    public int getTotalPortasAbertas()     { return totalPortasAbertas; }
}