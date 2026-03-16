package com.example.netshield_porta.viewmodel;

import com.example.netshield_porta.model.Porta;
import com.example.netshield_porta.repository.NetworkRepository;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.netshield_porta.model.Porta;
import com.example.netshield_porta.repository.NetworkRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortScannerViewModel extends ViewModel {

    private static final String TAG = "NetShield";
    private final NetworkRepository repository = new NetworkRepository();

    private final MutableLiveData<List<Porta>> _portasAbertas = new MutableLiveData<>();
    public final LiveData<List<Porta>> portasAbertas = _portasAbertas;

    private final MutableLiveData<Integer> _progresso = new MutableLiveData<>(0);
    public final LiveData<Integer> progresso = _progresso;

    private final MutableLiveData<Integer> _portaAtual = new MutableLiveData<>(0);
    public final LiveData<Integer> portaAtual = _portaAtual;

    private final MutableLiveData<Boolean> _escaneando = new MutableLiveData<>(false);
    public final LiveData<Boolean> escaneando = _escaneando;

    private final MutableLiveData<String> _erro = new MutableLiveData<>();
    public final LiveData<String> erro = _erro;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void escanearPortas(String ip, int inicio, int fim) {
        Log.d(TAG, "Iniciando scan: " + ip + " portas " + inicio + "-" + fim);
        _escaneando.postValue(true);
        _progresso.postValue(0);

        executor.execute(() -> {
            try {
                List<Porta> resultado = repository.escanear(ip, inicio, fim, (pct, porta) -> {
                    _progresso.postValue(pct);
                    _portaAtual.postValue(porta);
                    if (pct % 10 == 0) Log.d(TAG, "Progresso: " + pct + "% | Porta: " + porta);
                });
                Log.d(TAG, "Finalizado. Abertas: " + resultado.size());
                _portasAbertas.postValue(resultado);
            } catch (Exception e) {
                Log.e(TAG, "Erro: " + e.getMessage());
                _erro.postValue("Erro: " + e.getMessage());
            } finally {
                _escaneando.postValue(false);
                _progresso.postValue(100);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}