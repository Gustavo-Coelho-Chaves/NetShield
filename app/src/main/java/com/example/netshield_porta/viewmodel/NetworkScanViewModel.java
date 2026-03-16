package com.example.netshield_porta.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.netshield_porta.utils.NetworkScanner;

import java.util.ArrayList;
import java.util.List;

public class NetworkScanViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> _devices = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<String>> devices = _devices;

    private final MutableLiveData<Boolean> _scanning = new MutableLiveData<>(false);
    public final LiveData<Boolean> scanning = _scanning;

    private final MutableLiveData<Integer> _progress = new MutableLiveData<>(0);
    public final LiveData<Integer> progress = _progress;

    private final MutableLiveData<String> _gatewayIp = new MutableLiveData<>();
    public final LiveData<String> gatewayIp = _gatewayIp;

    private final MutableLiveData<String> _myIp = new MutableLiveData<>();
    public final LiveData<String> myIp = _myIp;

    public NetworkScanViewModel(@NonNull Application application) {
        super(application);
    }

    public void iniciarScan() {
        _scanning.postValue(true);
        _progress.postValue(0);
        _devices.postValue(new ArrayList<>());

        String gateway = NetworkScanner.getGatewayIp(getApplication());
        String myIp    = NetworkScanner.getMyIp(getApplication());
        _gatewayIp.postValue(gateway != null ? gateway : "Nao encontrado");
        _myIp.postValue(myIp != null ? myIp : "Nao encontrado");

        NetworkScanner.scanNetwork(getApplication(), new NetworkScanner.DeviceFoundListener() {

            @Override
            public void onDeviceFound(String ip, String mac) {
                List<String> current = _devices.getValue();
                if (current == null) current = new ArrayList<>();
                List<String> updated = new ArrayList<>(current);
                updated.add(ip);
                _devices.postValue(updated);
            }

            @Override
            public void onScanFinished(List<String> devices) {
                _scanning.postValue(false);
                _progress.postValue(100);
            }

            @Override
            public void onProgress(int current, int total) {
                int pct = (int) ((current / (float) total) * 100);
                _progress.postValue(pct);
            }
        });
    }
}