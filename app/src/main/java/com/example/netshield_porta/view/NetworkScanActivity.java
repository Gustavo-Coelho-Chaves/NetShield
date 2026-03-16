package com.example.netshield_porta.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netshield_porta.R;
import com.example.netshield_porta.utils.NetworkScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class NetworkScanActivity extends AppCompatActivity {

    private Button btnScan;
    private ProgressBar progressBar;
    private TextView txtProgresso, txtGateway, txtMeuIp, txtTotal;
    private LinearLayout listaDispositivos;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scan);

        btnScan           = findViewById(R.id.btnScanRede);
        progressBar       = findViewById(R.id.progressBarRede);
        txtProgresso      = findViewById(R.id.txtProgressoRede);
        txtGateway        = findViewById(R.id.txtGateway);
        txtMeuIp          = findViewById(R.id.txtMeuIp);
        txtTotal          = findViewById(R.id.txtTotalDispositivos);
        listaDispositivos = findViewById(R.id.listaDispositivos);

        String gateway = NetworkScanner.getGatewayIp(this);
        String myIp    = NetworkScanner.getMyIp(this);
        txtGateway.setText("Roteador: " + (gateway != null ? gateway : "--"));
        txtMeuIp.setText("Meu IP: " + (myIp != null ? myIp : "--"));

        btnScan.setOnClickListener(v -> iniciarScan());
    }

    private void iniciarScan() {
        btnScan.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtProgresso.setVisibility(View.VISIBLE);
        txtProgresso.setText("Varrendo rede...");
        listaDispositivos.removeAllViews();
        txtTotal.setText("Dispositivos encontrados: 0");

        final List<String> encontrados = new ArrayList<>();

        NetworkScanner.scanNetwork(this, new NetworkScanner.DeviceFoundListener() {

            @Override
            public void onDeviceFound(String ip, String mac) {
                mainHandler.post(() -> {
                    encontrados.add(ip);
                    txtTotal.setText("Dispositivos encontrados: " + encontrados.size());
                    adicionarDispositivo(ip, mac);
                });
            }

            @Override
            public void onScanFinished(List<String> devices) {
                mainHandler.post(() -> {
                    btnScan.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    txtProgresso.setVisibility(View.GONE);

                    if (encontrados.isEmpty()) {
                        txtTotal.setText("Nenhum dispositivo encontrado.");
                    }
                });
            }

            @Override
            public void onProgress(int current, int total) {
                mainHandler.post(() -> {
                    int pct = (int) ((current / (float) total) * 100);
                    progressBar.setProgress(pct);
                    txtProgresso.setText("Varrendo rede... " + pct + "%");
                });
            }
        });
    }

    private void adicionarDispositivo(String ip, String mac) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        card.setBackgroundColor(0xFF1E293B);


        TextView tvIp = new TextView(this);
        tvIp.setText("IP: " + ip);
        tvIp.setTextColor(0xFFFFFFFF);
        tvIp.setTextSize(15);
        tvIp.setTypeface(null, android.graphics.Typeface.BOLD);


        TextView tvMac = new TextView(this);
        tvMac.setText("MAC: " + mac);
        tvMac.setTextColor(0xFF94A3B8);
        tvMac.setTextSize(12);


        TextView tvScan = new TextView(this);
        tvScan.setText("Escanear portas →");
        tvScan.setTextColor(0xFF38BDF8);
        tvScan.setTextSize(13);
        tvScan.setPadding(0, 8, 0, 0);

        card.addView(tvIp);
        card.addView(tvMac);
        card.addView(tvScan);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, PortScannerActivity.class);
            intent.putExtra("IP_SELECIONADO", ip);
            startActivity(intent);
        });


        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(0xFF334155);

        listaDispositivos.addView(card);
        listaDispositivos.addView(divider);
    }
}