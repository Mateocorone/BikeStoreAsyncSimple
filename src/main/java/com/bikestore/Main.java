package com.bikestore;

public class Main {
    public static void main(String[] args) throws Exception {
        // Iniciar el consumidor en un hilo
        new Thread(() -> {
            try {
                new PaymentWorker().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "payment-worker").start();

        // Pequeña espera para que el consumidor quede listo
        Thread.sleep(1000);

        // Productor
        OrderProducer producer = new OrderProducer();

        // Pedido con probabilidad de éxito
        producer.send(new OrderMessage("ORD-1001", "cliente@demo.com", 149.90, false));

        // Pedido que forzamos a fallar (cae a DLQ tras 3 intentos)
        producer.send(new OrderMessage("ORD-FAIL", "cliente@demo.com", 99.99, true));
    }
}
