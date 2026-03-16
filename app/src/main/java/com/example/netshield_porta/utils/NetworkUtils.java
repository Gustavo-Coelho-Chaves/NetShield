package com.example.netshield_porta.utils;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtils {
    private static final int TIMEOUT_MS = 1000;

    public static boolean verificarPorta(String ip, int porta) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, porta), TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verificarHost(String ip) {
        if (verificarPorta(ip, 80))  return true;
        if (verificarPorta(ip, 443)) return true;
        return verificarPorta(ip, 22);
    }
}