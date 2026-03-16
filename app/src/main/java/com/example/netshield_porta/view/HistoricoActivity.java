package com.example.netshield_porta.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netshield_porta.R;
import com.example.netshield_porta.database.AppDatabase;
import com.example.netshield_porta.database.PortaEntity;
import com.example.netshield_porta.database.ScanEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout listaHistorico;
    private TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        listaHistorico = findViewById(R.id.listaHistorico);
        txtVazio       = findViewById(R.id.txtVazio);

        carregarHistorico();
    }

    private void carregarHistorico() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<ScanEntity> scans = db.scanDao().listarScans();

            List<List<PortaEntity>> todasPortas = new ArrayList<>();
            for (ScanEntity scan : scans) {
                List<PortaEntity> portas = db.scanDao().listarPortas(scan.id);
                todasPortas.add(portas);
            }

            runOnUiThread(() -> {
                if (scans.isEmpty()) {
                    txtVazio.setVisibility(View.VISIBLE);
                    return;
                }
                for (int i = 0; i < scans.size(); i++) {
                    adicionarCard(scans.get(i), todasPortas.get(i));
                }
            });
        });
    }

    private void adicionarCard(ScanEntity scan, List<PortaEntity> portas) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(0xFF1E293B);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);

        TextView tvIp = new TextView(this);
        tvIp.setText(scan.ipAlvo + "  [" + scan.tipoScan + "]");
        tvIp.setTextColor(0xFFFFFFFF);
        tvIp.setTextSize(15);
        tvIp.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvData = new TextView(this);
        tvData.setText(scan.dataScan);
        tvData.setTextColor(0xFF94A3B8);
        tvData.setTextSize(12);

        int cor = scan.classificacao.equals("SEGURO") ? 0xFF16A34A :
                scan.classificacao.equals("MEDIO")  ? 0xFFEAB308 : 0xFFDC2626;

        TextView tvScore = new TextView(this);
        tvScore.setText("Score: " + scan.score + "/100  —  " + scan.classificacao);
        tvScore.setTextColor(cor);
        tvScore.setTextSize(13);

        TextView tvPortas = new TextView(this);
        tvPortas.setText("Portas abertas: " + scan.totalPortas +
                "  |  Criticas: " + scan.portasCriticas);
        tvPortas.setTextColor(0xFF94A3B8);
        tvPortas.setTextSize(12);
        tvPortas.setPadding(0, 4, 0, 8);

        TextView tvVer = new TextView(this);
        tvVer.setText("Ver relatorio completo →");
        tvVer.setTextColor(0xFF38BDF8);
        tvVer.setTextSize(13);

        card.addView(tvIp);
        card.addView(tvData);
        card.addView(tvScore);
        card.addView(tvPortas);
        card.addView(tvVer);

        card.setOnClickListener(v -> {
            List<Integer> vermelhas = new ArrayList<>();
            List<Integer> amarelas  = new ArrayList<>();
            List<Integer> verdes    = new ArrayList<>();

            for (PortaEntity p : portas) {
                if (p.nivelRisco.equals("VERMELHO"))      vermelhas.add(p.numeroPorta);
                else if (p.nivelRisco.equals("AMARELO"))  amarelas.add(p.numeroPorta);
                else                                       verdes.add(p.numeroPorta);
            }

            int[] arrVermelhas = new int[vermelhas.size()];
            for (int i = 0; i < vermelhas.size(); i++) arrVermelhas[i] = vermelhas.get(i);

            int[] arrAmarelas = new int[amarelas.size()];
            for (int i = 0; i < amarelas.size(); i++) arrAmarelas[i] = amarelas.get(i);

            int[] arrVerdes = new int[verdes.size()];
            for (int i = 0; i < verdes.size(); i++) arrVerdes[i] = verdes.get(i);

            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("PORTAS_ABERTAS",  scan.totalPortas);
            intent.putExtra("PORTAS_CRITICAS", scan.portasCriticas);
            intent.putExtra("SCORE",           scan.score);
            intent.putExtra("CLASSIFICACAO",   scan.classificacao);
            intent.putExtra("LISTA_VERMELHAS", arrVermelhas);
            intent.putExtra("LISTA_AMARELAS",  arrAmarelas);
            intent.putExtra("LISTA_VERDES",    arrVerdes);
            startActivity(intent);
        });

        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(0xFF334155);

        listaHistorico.addView(card);
        listaHistorico.addView(divider);
    }
}