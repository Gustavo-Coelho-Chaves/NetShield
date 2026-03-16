package com.example.netshield_porta.view;

import com.example.netshield_porta.database.AppDatabase;
import com.example.netshield_porta.database.ScanEntity;
import com.example.netshield_porta.database.PortaEntity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.netshield_porta.R;
import com.example.netshield_porta.model.Porta;
import com.example.netshield_porta.model.RelatorioSeguranca;
import com.example.netshield_porta.utils.ScanStorage;
import com.example.netshield_porta.viewmodel.PortScannerViewModel;
import com.example.netshield_porta.viewmodel.SecurityViewModel;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PortScannerActivity extends AppCompatActivity {

    private EditText edtIp, edtDominio, edtPortaInicio, edtPortaFim;
    private Button btnScan, btnModoLocal, btnModoExterno;
    private TextView txtResultado, txtProgresso, txtAvisoExterno;
    private ProgressBar progressBar;
    private LinearLayout layoutLocal, layoutExterno;
    private PortScannerViewModel scanViewModel;
    private SecurityViewModel securityViewModel;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean modoExterno = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_scanner);

        edtIp           = findViewById(R.id.edtIp);
        edtDominio      = findViewById(R.id.edtDominio);
        edtPortaInicio  = findViewById(R.id.edtPortaInicio);
        edtPortaFim     = findViewById(R.id.edtPortaFim);
        btnScan         = findViewById(R.id.btnScan);
        btnModoLocal    = findViewById(R.id.btnModoLocal);
        btnModoExterno  = findViewById(R.id.btnModoExterno);
        txtResultado    = findViewById(R.id.txtResultado);
        progressBar     = findViewById(R.id.progressBar);
        txtProgresso    = findViewById(R.id.txtProgresso);
        txtAvisoExterno = findViewById(R.id.txtAvisoExterno);
        layoutLocal     = findViewById(R.id.layoutLocal);
        layoutExterno   = findViewById(R.id.layoutExterno);

        scanViewModel     = new ViewModelProvider(this).get(PortScannerViewModel.class);
        securityViewModel = new ViewModelProvider(this).get(SecurityViewModel.class);

        // Receber IP se vier do scan de rede
        String ipSelecionado = getIntent().getStringExtra("IP_SELECIONADO");
        if (ipSelecionado != null) edtIp.setText(ipSelecionado);

        btnModoLocal.setOnClickListener(v -> setModo(false));
        btnModoExterno.setOnClickListener(v -> setModo(true));

        scanViewModel.progresso.observe(this, pct -> {
            if (pct != null) progressBar.setProgress(pct);
        });

        scanViewModel.portaAtual.observe(this, porta -> {
            if (porta != null && porta > 0)
                txtProgresso.setText("Testando porta " + porta + "...");
        });

        scanViewModel.escaneando.observe(this, escaneando -> {
            if (escaneando == null) return;
            btnScan.setEnabled(!escaneando);
            progressBar.setVisibility(escaneando ? View.VISIBLE : View.GONE);
            txtProgresso.setVisibility(escaneando ? View.VISIBLE : View.GONE);
            if (escaneando) txtResultado.setText("Escaneando, aguarde...");
        });

        scanViewModel.erro.observe(this, msg -> {
            if (msg != null && !msg.isEmpty())
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });

        scanViewModel.portasAbertas.observe(this, portas -> {
            if (portas == null) return;
            exibirResultado(portas);
        });

        btnScan.setOnClickListener(v -> iniciarScan());
    }

    private void setModo(boolean externo) {
        modoExterno = externo;
        if (externo) {
            btnModoExterno.setBackgroundTintList(
                    getResources().getColorStateList(R.color.verdeSeguro, null));
            btnModoExterno.setTextColor(0xFFFFFFFF);
            btnModoLocal.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF0F172A));
            btnModoLocal.setTextColor(0xFF94A3B8);
            layoutLocal.setVisibility(View.GONE);
            layoutExterno.setVisibility(View.VISIBLE);
            txtAvisoExterno.setVisibility(View.VISIBLE);
        } else {
            btnModoLocal.setBackgroundTintList(
                    getResources().getColorStateList(R.color.verdeSeguro, null));
            btnModoLocal.setTextColor(0xFFFFFFFF);
            btnModoExterno.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF0F172A));
            btnModoExterno.setTextColor(0xFF94A3B8);
            layoutLocal.setVisibility(View.VISIBLE);
            layoutExterno.setVisibility(View.GONE);
            txtAvisoExterno.setVisibility(View.GONE);
        }
    }

    private void iniciarScan() {
        String ini = edtPortaInicio.getText().toString().trim();
        String fim = edtPortaFim.getText().toString().trim();

        if (ini.isEmpty() || fim.isEmpty()) {
            Toast.makeText(this, "Preencha as portas.", Toast.LENGTH_SHORT).show();
            return;
        }

        int inicio, fimInt;
        try {
            inicio = Integer.parseInt(ini);
            fimInt = Integer.parseInt(fim);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Portas devem ser numeros.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (modoExterno) {
            String dominio = edtDominio.getText().toString().trim();
            if (dominio.isEmpty()) {
                Toast.makeText(this, "Digite um IP ou dominio.", Toast.LENGTH_SHORT).show();
                return;
            }

            txtResultado.setText("Resolvendo dominio " + dominio + "...");
            btnScan.setEnabled(false);

            final int inicioFinal = inicio;
            final int fimFinal    = fimInt;

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    InetAddress address = InetAddress.getByName(dominio);
                    String ip = address.getHostAddress();
                    mainHandler.post(() -> {
                        txtResultado.setText("IP resolvido: " + ip + "\nIniciando scan...");
                        iniciarScanNoIp(ip, inicioFinal, fimFinal);
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> {
                        txtResultado.setText("Nao foi possivel resolver: " + dominio);
                        btnScan.setEnabled(true);
                    });
                }
            });
        } else {
            String ip = edtIp.getText().toString().trim();
            if (ip.isEmpty()) {
                Toast.makeText(this, "Digite um IP.", Toast.LENGTH_SHORT).show();
                return;
            }
            iniciarScanNoIp(ip, inicio, fimInt);
        }
    }

    private void iniciarScanNoIp(String ip, int inicio, int fim) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtProgresso.setVisibility(View.VISIBLE);
        txtProgresso.setText("Iniciando scan...");
        btnScan.setEnabled(false);
        scanViewModel.escanearPortas(ip, inicio, fim);
    }

    private void exibirResultado(List<Porta> portas) {
        progressBar.setVisibility(View.GONE);
        txtProgresso.setVisibility(View.GONE);
        btnScan.setEnabled(true);

        if (portas.isEmpty()) {
            txtResultado.setText(
                    "Nenhuma porta aberta encontrada.\n\n" +
                            "Possiveis motivos:\n" +
                            "- Firewall bloqueando conexoes\n" +
                            "- IP incorreto\n" +
                            "- Host offline");
            return;
        }

        List<Integer> vermelhas = new ArrayList<>();
        List<Integer> amarelas  = new ArrayList<>();
        List<Integer> verdes    = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("Portas abertas: ").append(portas.size()).append("\n\n");

        for (Porta p : portas) {
            sb.append("Porta ").append(p.getNumero())
                    .append(" [").append(p.getServico()).append("]")
                    .append(" - ").append(p.getNivelRisco()).append("\n");

            switch (p.getNivelRisco()) {
                case "VERMELHO": vermelhas.add(p.getNumero()); break;
                case "AMARELO":  amarelas.add(p.getNumero());  break;
                default:         verdes.add(p.getNumero());    break;
            }
        }

        txtResultado.setText(sb.toString());

        RelatorioSeguranca rel = securityViewModel.gerarRelatorio(portas);

        int[] arrVermelhas = new int[vermelhas.size()];
        for (int i = 0; i < vermelhas.size(); i++) arrVermelhas[i] = vermelhas.get(i);

        int[] arrAmarelas = new int[amarelas.size()];
        for (int i = 0; i < amarelas.size(); i++) arrAmarelas[i] = amarelas.get(i);

        int[] arrVerdes = new int[verdes.size()];
        for (int i = 0; i < verdes.size(); i++) arrVerdes[i] = verdes.get(i);

        mainHandler.postDelayed(() -> {

            ScanStorage.salvar(
                    portas.size(),
                    vermelhas.size(),
                    rel.getScore(),
                    rel.getClassificacao(),
                    arrVermelhas,
                    arrAmarelas,
                    arrVerdes
            );

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);

                ScanEntity scan = new ScanEntity();
                scan.ipAlvo        = modoExterno ?
                        edtDominio.getText().toString().trim() :
                        edtIp.getText().toString().trim();
                scan.tipoScan      = modoExterno ? "EXTERNO" : "LOCAL";
                scan.dataScan      = new java.text.SimpleDateFormat(
                        "dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date());
                scan.totalPortas   = portas.size();
                scan.score         = rel.getScore();
                scan.classificacao = rel.getClassificacao();
                scan.portasCriticas = vermelhas.size();
                scan.portasMedias   = amarelas.size();
                scan.portasSeguras  = verdes.size();

                long idScan = db.scanDao().inserirScan(scan);

                for (Porta p : portas) {
                    PortaEntity pe = new PortaEntity();
                    pe.idScan      = (int) idScan;
                    pe.numeroPorta = p.getNumero();
                    pe.servico     = p.getServico();
                    pe.nivelRisco  = p.getNivelRisco();
                    db.scanDao().inserirPorta(pe);
                }
            });

            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("PORTAS_ABERTAS",  portas.size());
            intent.putExtra("PORTAS_CRITICAS", vermelhas.size());
            intent.putExtra("SCORE",           rel.getScore());
            intent.putExtra("CLASSIFICACAO",   rel.getClassificacao());
            intent.putExtra("LISTA_VERMELHAS", arrVermelhas);
            intent.putExtra("LISTA_AMARELAS",  arrAmarelas);
            intent.putExtra("LISTA_VERDES",    arrVerdes);
            startActivity(intent);
        }, 1500);
    }
}