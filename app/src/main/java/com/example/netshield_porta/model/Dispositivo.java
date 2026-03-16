package com.example.netshield_porta.model;
import java.util.List;

public class Dispositivo {
    private String ip;
    private boolean ativo;
    private List<Porta> portas;

    public Dispositivo(String ip, boolean ativo) {
        this.ip = ip;
        this.ativo = ativo;
    }

    public String getIp()          { return ip; }
    public boolean isAtivo()       { return ativo; }
    public List<Porta> getPortas() { return portas; }
    public void setPortas(List<Porta> portas) { this.portas = portas; }
}