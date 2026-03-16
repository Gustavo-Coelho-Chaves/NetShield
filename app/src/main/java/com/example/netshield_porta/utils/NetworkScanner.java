package com.example.netshield_porta.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkScanner {

    public interface DeviceFoundListener {
        void onDeviceFound(String ip, String mac);
        void onScanFinished(List<String> devices);
        void onProgress(int current, int total);
    }

    public static String getNetworkPrefix(Context context) {
        WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ip = dhcpInfo.gateway;
        return String.format("%d.%d.%d.",
                (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff));
    }

    public static String getGatewayIp(Context context) {
        WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ip = dhcpInfo.gateway;
        return String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
    }

    public static String getMyIp(Context context) {
        WifiManager wifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return null;
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        return String.format("%d.%d.%d.%d",
                (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
    }

    public static void scanNetwork(Context context, DeviceFoundListener listener) {
        String prefix = getNetworkPrefix(context);
        if (prefix == null) {
            listener.onScanFinished(new ArrayList<>());
            return;
        }

        List<String> found = new ArrayList<>();
        int total = 254;
        int[] counter = {0};

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 1; i <= 254; i++) {
            final String ip = prefix + i;
            Future<?> f = executor.submit(() -> {
                boolean ativo = isHostReachable(ip);
                if (ativo) {
                    String mac = getMacFromArp(ip);
                    synchronized (found) {
                        if (!found.contains(ip)) {
                            found.add(ip);
                            listener.onDeviceFound(ip, mac);
                        }
                    }
                }
                synchronized (counter) {
                    counter[0]++;
                    listener.onProgress(counter[0], total);
                }
            });
            futures.add(f);
        }

        new Thread(() -> {
            for (Future<?> f : futures) {
                try { f.get(); } catch (Exception ignored) {}
            }
            executor.shutdown();

            // Depois da varredura TCP, complementar com ARP
            List<String[]> arpEntries = lerTabelaArp();
            for (String[] entry : arpEntries) {
                String ip  = entry[0];
                String mac = entry[1];
                if (ip.startsWith(prefix)) {
                    synchronized (found) {
                        if (!found.contains(ip)) {
                            found.add(ip);
                            listener.onDeviceFound(ip, mac);
                        }
                    }
                }
            }

            listener.onScanFinished(found);
        }).start();
    }

    private static boolean isHostReachable(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address.isReachable(500)) return true;
        } catch (Exception ignored) {}

        int[] ports = {80, 443, 22, 8080, 21, 23, 53, 135, 139, 445, 3389, 8443, 5000};
        for (int port : ports) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, port), 400);
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    private static String getMacFromArp(String ip) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/arp"));
            String linha;
            reader.readLine();
            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.trim().split("\\s+");
                if (campos.length >= 4 && campos[0].equals(ip)) {
                    reader.close();
                    return campos[3];
                }
            }
            reader.close();
        } catch (Exception ignored) {}
        return "N/A";
    }

    public static List<String[]> lerTabelaArp() {
        List<String[]> dispositivos = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/arp"));
            String linha;
            reader.readLine();
            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.trim().split("\\s+");
                if (campos.length >= 4) {
                    String ip  = campos[0];
                    String mac = campos[3];
                    if (!mac.equals("00:00:00:00:00:00") && !mac.equals("*")) {
                        dispositivos.add(new String[]{ip, mac});
                    }
                }
            }
            reader.close();
        } catch (Exception ignored) {}
        return dispositivos;
    }
}