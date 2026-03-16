package com.example.netshield_porta.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.netshield_porta.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        CardView cardScanner     = findViewById(R.id.cardScanner);
        CardView cardRelatorio   = findViewById(R.id.cardRelatorio);
        CardView cardRedeScanner = findViewById(R.id.cardRedeScanner);
        CardView cardHistorico   = findViewById(R.id.cardHistorico);

        cardScanner.setOnClickListener(v ->
                startActivity(new Intent(this, PortScannerActivity.class)));
        cardRelatorio.setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));
        cardRedeScanner.setOnClickListener(v ->
                startActivity(new Intent(this, NetworkScanActivity.class)));
        cardHistorico.setOnClickListener(v ->
                startActivity(new Intent(this, HistoricoActivity.class)));
    }
}