package com.bikestore;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

public class PaymentWorker {

    private static final String QUEUE = "order.queue";
    private static final String DLQ   = "order.dlq";

    // Contador de reintentos por pedidoId (en memoria)
    private final Map<String, Integer> retries = new ConcurrentHashMap<>();

    public void start() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declaración de colas
        channel.queueDeclare(QUEUE, true, false, false, null);
        channel.queueDeclare(DLQ,   true, false, false, null);

        // Prefetch 1 para procesar de a uno
        channel.basicQos(1);

        DeliverCallback deliver = (tag, delivery) -> {
            String json = new String(delivery.getBody());
            OrderMessage msg = new Gson().fromJson(json, OrderMessage.class);

            // 50% éxito si no se fuerza fallo
            boolean paid = !msg.forceFail && ThreadLocalRandom.current().nextBoolean();

            if (paid) {
                LogFmt.log(msg.pedidoId, "PaymentWorker -> PAID");
                new EmailWorker().sendEmail(msg);
                retries.remove(msg.pedidoId);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                return;
            }

            // Manejo de reintentos
            int count = retries.getOrDefault(msg.pedidoId, 0) + 1;
            retries.put(msg.pedidoId, count);
            LogFmt.log(msg.pedidoId, "PaymentWorker -> FAIL intento " + count);

            if (count >= 3) {
                // Enviar a DLQ
                channel.basicPublish("", DLQ, null, json.getBytes());
                LogFmt.log(msg.pedidoId, "PaymentWorker -> enviado a DLQ");
                retries.remove(msg.pedidoId);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } else {
                // Reencolar para reintentar
                channel.basicPublish("", QUEUE, null, json.getBytes());
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        CancelCallback cancel = tag -> { };

        // AutoAck = false (queremos ACK manual)
        channel.basicConsume(QUEUE, false, deliver, cancel);
        LogFmt.log("-", "PaymentWorker -> escuchando en " + QUEUE);
    }
}
