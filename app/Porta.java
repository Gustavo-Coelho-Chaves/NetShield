package com.netshield.model;

public class Porta {
    private int numero;
    private boolean aberta;
    private String nivelRisco;
    private String servico;

    public Porta(int numero, boolean aberta, String nivelRisco) {
        this.numero = numero;
        this.aberta = aberta;
        this.nivelRisco = nivelRisco;
        this.servico = identificarServico(numero);
    }

    private String identificarServico(int porta) {
        switch (porta) {
            case 21:   return "FTP";
            case 22:   return "SSH";
            case 23:   return "Telnet";
            case 25:   return "SMTP";
            case 53:   return "DNS";
            case 80:   return "HTTP";
            case 110:  return "POP3";
            case 143:  return "IMAP";
            case 443:  return "HTTPS";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 5900: return "VNC";
            case 8080: return "HTTP-Alt";
            default:   return "Desconhecido";
        }
    }

    public int getNumero()        { return numero; }
    public boolean isAberta()     { return aberta; }
    public String getNivelRisco() { return nivelRisco; }
    public String getServico()    { return servico; }
}