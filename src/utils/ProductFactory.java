package utils;

import models.Product;

public class ProductFactory {
    public Product createProduct(String type) {
        switch (type.toLowerCase()) {
            case "electronics":
                return new Product("1", "Laptop", 1000.0);
            case "clothing":
                return new Product("2", "T-Shirt", 20.0);
            default:
                return null;
        }
    }
}