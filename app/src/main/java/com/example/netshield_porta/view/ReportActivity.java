package com.example.netshield_porta.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netshield_porta.R;
import com.example.netshield_porta.utils.ScanStorage;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        TextView txtClassificacao  = findViewById(R.id.txtClassificacao);
        TextView txtScore          = findViewById(R.id.txtScore);
        TextView txtPortasAbertas  = findViewById(R.id.txtPortasAbertas);
        TextView txtPortasCriticas = findViewById(R.id.txtPortasCriticas);
        TextView txtDescricao      = findViewById(R.id.txtDescricao);
        TextView txtVermelhas      = findViewById(R.id.txtVermelhas);
        TextView txtAmarelas       = findViewById(R.id.txtAmarelas);
        TextView txtVerdes         = findViewById(R.id.txtVerdes);

        int portasAbertas = getIntent().getIntExtra("PORTAS_ABERTAS", -1);


        if (portasAbertas == -1 && ScanStorage.temDados()) {
            portasAbertas = ScanStorage.getPortasAbertas();
        }


        if (portasAbertas == -1) {
            txtClassificacao.setText("SEM DADOS");
            txtClassificacao.setTextColor(0xFF94A3B8);
            txtScore.setText("Nenhum scan realizado ainda");
            txtPortasAbertas.setText("");
            txtPortasCriticas.setText("");
            txtDescricao.setText("Va em Scanner de Portas, preencha o IP e inicie o scan para ver o relatorio aqui.");
            txtVermelhas.setText("");
            txtAmarelas.setText("");
            txtVerdes.setText("");
            return;
        }


        int portasCriticas   = getIntent().getIntExtra("PORTAS_CRITICAS", ScanStorage.getPortasCriticas());
        int score            = getIntent().getIntExtra("SCORE", ScanStorage.getScore());
        String classificacao = getIntent().getStringExtra("CLASSIFICACAO");
        if (classificacao == null) classificacao = ScanStorage.getClassificacao();

        int[] portasV  = getIntent().getIntArrayExtra("LISTA_VERMELHAS");
        int[] portasA  = getIntent().getIntArrayExtra("LISTA_AMARELAS");
        int[] portasVe = getIntent().getIntArrayExtra("LISTA_VERDES");

        if (portasV  == null) portasV  = ScanStorage.getListaVermelhas();
        if (portasA  == null) portasA  = ScanStorage.getListaAmarelas();
        if (portasVe == null) portasVe = ScanStorage.getListaVerdes();

        txtScore.setText("Score: " + score + "/100");
        txtPortasAbertas.setText("Total de portas abertas: " + portasAbertas);
        txtPortasCriticas.setText("Portas criticas: " + portasCriticas);

        switch (classificacao) {
            case "SEGURO":
                txtClassificacao.setText("SEGURO");
                txtClassificacao.setTextColor(0xFF16A34A);
                txtDescricao.setText("Rede com baixo risco. Nenhuma porta critica detectada.");
                break;
            case "MEDIO":
            case "MÉDIO":
                txtClassificacao.setText("MEDIO");
                txtClassificacao.setTextColor(0xFFEAB308);
                txtDescricao.setText("Risco moderado. Algumas portas de atencao detectadas.");
                break;
            case "FRAGIL":
            case "FRÁGIL":
                txtClassificacao.setText("FRAGIL");
                txtClassificacao.setTextColor(0xFFDC2626);
                txtDescricao.setText("Rede vulneravel! Portas criticas abertas. Acao imediata recomendada.");
                break;
            default:
                txtClassificacao.setText("SEGURO");
                txtClassificacao.setTextColor(0xFF16A34A);
                txtDescricao.setText("Rede com baixo risco.");
        }


        if (portasV.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int p : portasV) {
                sb.append("Porta ").append(p)
                        .append(" — ").append(getNomeServico(p)).append("\n")
                        .append(getDescricaoServico(p)).append("\n\n");
            }
            txtVermelhas.setText(sb.toString());
            txtVermelhas.setTextColor(0xFFDC2626);
        } else {
            txtVermelhas.setText("Nenhuma porta critica detectada.");
            txtVermelhas.setTextColor(0xFF16A34A);
        }


        if (portasA.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int p : portasA) {
                sb.append("Porta ").append(p)
                        .append(" — ").append(getNomeServico(p)).append("\n")
                        .append(getDescricaoServico(p)).append("\n\n");
            }
            txtAmarelas.setText(sb.toString());
            txtAmarelas.setTextColor(0xFFEAB308);
        } else {
            txtAmarelas.setText("Nenhuma porta de atencao detectada.");
            txtAmarelas.setTextColor(0xFF16A34A);
        }


        if (portasVe.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int p : portasVe) {
                sb.append("Porta ").append(p)
                        .append(" — ").append(getNomeServico(p)).append("\n")
                        .append(getDescricaoServico(p)).append("\n\n");
            }
            txtVerdes.setText(sb.toString());
            txtVerdes.setTextColor(0xFF16A34A);
        } else {
            txtVerdes.setText("Nenhuma porta segura detectada.");
            txtVerdes.setTextColor(0xFF94A3B8);
        }
    }

    private String getNomeServico(int porta) {
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
            default:   return "Servico desconhecido";
        }
    }

    private String getDescricaoServico(int porta) {
        switch (porta) {
            case 21:   return "Transferencia de arquivos sem criptografia. Risco de interceptacao.";
            case 22:   return "Acesso remoto seguro via SSH. Mantenha senha forte.";
            case 23:   return "Acesso remoto SEM criptografia. Altamente vulneravel. Desative!";
            case 25:   return "Envio de emails. Pode ser explorado para spam.";
            case 53:   return "Resolucao de nomes DNS. Necessario para navegacao.";
            case 80:   return "Servidor web sem criptografia. Dados trafegam abertos.";
            case 110:  return "Recebimento de emails sem criptografia.";
            case 143:  return "Acesso a emails sem criptografia.";
            case 443:  return "Servidor web seguro com SSL/TLS. Recomendado.";
            case 3306: return "Banco de dados MySQL exposto. Risco alto se publico.";
            case 3389: return "Acesso remoto Windows RDP. Alvo frequente de ataques. Desative!";
            case 5900: return "Controle remoto VNC sem criptografia. Desative se nao usar!";
            case 8080: return "Servidor web alternativo. Verifique se e necessario.";
            default:   return "Servico nao identificado. Verifique se e necessario.";
        }
    }
}