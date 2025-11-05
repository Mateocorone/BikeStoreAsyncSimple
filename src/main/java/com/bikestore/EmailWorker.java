package com.bikestore;

public class EmailWorker {
    public void sendEmail(OrderMessage msg) {
        LogFmt.log(msg.pedidoId, "EmailWorker -> enviado email a " + msg.email + " por $" + msg.total);
    }
}
