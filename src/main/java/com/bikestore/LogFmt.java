package com.bikestore;

import java.time.Instant;

public class LogFmt {
    public static void log(String pedidoId, String step) {
        System.out.printf("[pedidoId=%s][ts=%s][thread=%s] %s%n",
                pedidoId, Instant.now(), Thread.currentThread().getName(), step);
    }
}
