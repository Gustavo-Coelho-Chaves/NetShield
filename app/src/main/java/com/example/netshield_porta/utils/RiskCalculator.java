package com.example.netshield_porta.utils;
public class RiskCalculator {

    public static String classificarPorta(int porta) {
        switch (porta) {
            case 23: case 3389: case 5900: return "VERMELHO";
            case 21: case 25: case 80:
            case 110: case 143: case 3306: case 8080: return "AMARELO";
            default: return "VERDE";
        }
    }

    public static String classificarScore(int score) {
        if (score >= 80)      return "SEGURO";
        else if (score >= 50) return "MÉDIO";
        else                  return "FRÁGIL";
    }
}