package models;

import java.util.List;

public class Order {
    private String orderId;
    private List<Product> products;
    private double totalAmount;

    public Order(String orderId, List<Product> products, double totalAmount) {
        this.orderId = orderId;
        this.products = products;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}