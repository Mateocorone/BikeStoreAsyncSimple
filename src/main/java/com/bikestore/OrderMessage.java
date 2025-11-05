package com.bikestore;

public class OrderMessage {
    public String pedidoId;
    public String email;
    public double total;
    public boolean forceFail;

    public OrderMessage(String pedidoId, String email, double total, boolean forceFail) {
        this.pedidoId = pedidoId;
        this.email = email;
        this.total = total;
        this.forceFail = forceFail;
    }
}
