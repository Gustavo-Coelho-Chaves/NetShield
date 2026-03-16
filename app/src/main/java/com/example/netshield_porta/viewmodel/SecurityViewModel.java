package com.example.netshield_porta.viewmodel;

import com.example.netshield_porta.model.Porta;
import com.example.netshield_porta.utils.RiskCalculator;
import androidx.lifecycle.ViewModel;

import com.example.netshield_porta.model.RelatorioSeguranca;

import java.util.ArrayList;
import java.util.List;

public class SecurityViewModel extends ViewModel {

    public RelatorioSeguranca gerarRelatorio(List<Porta> portasAbertas) {
        int score = 100;
        List<Porta> portasCriticas = new ArrayList<>();

        for (Porta p : portasAbertas) {
            switch (p.getNivelRisco()) {
                case "VERMELHO": score -= 40; portasCriticas.add(p); break;
                case "AMARELO":  score -= 15; portasCriticas.add(p); break;
            }
        }

        if (score < 0) score = 0;
        String classificacao = RiskCalculator.classificarScore(score);
        return new RelatorioSeguranca(score, classificacao, portasCriticas, portasAbertas.size());
    }
}