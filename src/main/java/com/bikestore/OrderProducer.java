package com.bikestore;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class OrderProducer {

    private static final String QUEUE = "order.queue";

    public void send(OrderMessage message) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // RabbitMQ local
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Cola duradera
            channel.queueDeclare(QUEUE, true, false, false, null);

            String json = new Gson().toJson(message);
            channel.basicPublish("", QUEUE, null, json.getBytes());
            LogFmt.log(message.pedidoId, "OrderProducer -> enviado a order.queue");
        }
    }
}
