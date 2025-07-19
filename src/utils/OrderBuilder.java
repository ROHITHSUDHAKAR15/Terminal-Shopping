package utils;

import models.Order;
import models.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private List<Product> products = new ArrayList<>();

    public OrderBuilder addProduct(Product product) {
        products.add(product);
        return this;
    }

    public Order build() {
        double totalAmount = products.stream().mapToDouble(Product::getPrice).sum();
        return new Order("order1", products, totalAmount);
    }
}