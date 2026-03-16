package com.example.netshield_porta.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_scan")
public class ScanEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String ipAlvo;
    public String tipoScan;
    public String dataScan;
    public int totalPortas;
    public int score;
    public String classificacao;
    public int portasCriticas;
    public int portasMedias;
    public int portasSeguras;
}