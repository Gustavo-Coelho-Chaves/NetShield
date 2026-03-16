package com.example.netshield_porta.repository;

import com.example.netshield_porta.model.Porta;
import com.example.netshield_porta.utils.NetworkUtils;
import com.example.netshield_porta.utils.RiskCalculator;
import java.util.ArrayList;
import java.util.List;

public class NetworkRepository {

    public List<Porta> escanear(String ip, int inicio, int fim, ScanProgressListener listener) {
        List<Porta> portasAbertas = new ArrayList<>();
        int total = fim - inicio + 1;

        for (int i = inicio; i <= fim; i++) {
            boolean aberta = NetworkUtils.verificarPorta(ip, i);
            if (aberta) {
                String risco = RiskCalculator.classificarPorta(i);
                portasAbertas.add(new Porta(i, true, risco));
            }
            if (listener != null) {
                int progresso = (int) (((i - inicio + 1) / (float) total) * 100);
                listener.onProgresso(progresso, i);
            }
        }
        return portasAbertas;
    }

    public interface ScanProgressListener {
        void onProgresso(int porcentagem, int portaAtual);
    }
}