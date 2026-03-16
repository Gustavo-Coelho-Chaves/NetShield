package com.example.netshield_porta.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScanDao {

    @Insert
    long inserirScan(ScanEntity scan);

    @Insert
    void inserirPorta(PortaEntity porta);

    @Query("SELECT * FROM tb_scan ORDER BY id DESC")
    List<ScanEntity> listarScans();

    @Query("SELECT * FROM tb_porta WHERE idScan = :idScan")
    List<PortaEntity> listarPortas(int idScan);

    @Query("DELETE FROM tb_scan WHERE id = :id")
    void deletarScan(int id);

    @Query("DELETE FROM tb_scan")
    void limparTudo();
}