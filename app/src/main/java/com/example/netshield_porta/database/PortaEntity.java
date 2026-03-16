package com.example.netshield_porta.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tb_porta",
        foreignKeys = @ForeignKey(
                entity = ScanEntity.class,
                parentColumns = "id",
                childColumns = "idScan",
                onDelete = ForeignKey.CASCADE
        )
)
public class PortaEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int idScan;
    public int numeroPorta;
    public String servico;
    public String nivelRisco;
}