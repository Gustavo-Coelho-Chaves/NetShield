package com.example.netshield_porta.utils;

public class ScanStorage {

    private static int portasAbertas  = -1;
    private static int portasCriticas = 0;
    private static int score          = 0;
    private static String classificacao = "";
    private static int[] listaVermelhas = new int[0];
    private static int[] listaAmarelas  = new int[0];
    private static int[] listaVerdes    = new int[0];

    public static void salvar(int abertas, int criticas, int sc,
                              String classif, int[] vermelhas,
                              int[] amarelas, int[] verdes) {
        portasAbertas   = abertas;
        portasCriticas  = criticas;
        score           = sc;
        classificacao   = classif;
        listaVermelhas  = vermelhas;
        listaAmarelas   = amarelas;
        listaVerdes     = verdes;
    }

    public static boolean temDados()          { return portasAbertas >= 0; }
    public static int getPortasAbertas()      { return portasAbertas; }
    public static int getPortasCriticas()     { return portasCriticas; }
    public static int getScore()              { return score; }
    public static String getClassificacao()   { return classificacao; }
    public static int[] getListaVermelhas()   { return listaVermelhas; }
    public static int[] getListaAmarelas()    { return listaAmarelas; }
    public static int[] getListaVerdes()      { return listaVerdes; }
}