package controllers;

import models.Product;
import services.UserService;
import views.CLIView;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingController {
    private CLIView view;
    private UserService userService;
    private Connection connection;
    private List<Product> cart;

    public ShoppingController(CLIView view, UserService userService) {
        this.view = view;
        this.userService = userService;
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.cart = new ArrayList<>();
    }

    public void start() {
        view.displayMessage("Welcome to the Shopping App!");
        String choice = view.getInput("Do you want to (1) Login or (2) Register? ");
        if (choice.equals("1")) {
            if (login()) {
                selectCategory();
            }
        } else if (choice.equals("2")) {
            if (register()) {
                selectCategory();
            }
        }
    }

    private boolean login() {
        String email = view.getInput("Enter your email: ");
        String password = view.getInput("Enter your password: ");
        if (userService.loginUser(email, password)) {
            view.displayMessage("Login successful!");
            return true;
        } else {
            view.displayMessage("Invalid credentials.");
            return false;
        }
    }

    private boolean register() {
        String email = view.getInput("Enter your email: ");
        String password = view.getInput("Enter your password: ");
        if (userService.registerUser(email, password)) {
            view.displayMessage("Registration successful!");
            return true;
        } else {
            view.displayMessage("Registration failed.");
            return false;
        }
    }

    private void selectCategory() {
        view.displayMessage("Select a category:");
        view.displayMessage("1. Grocery");
        view.displayMessage("2. Devices");
        view.displayMessage("3. Fashion");
        view.displayMessage("4. Dessert");
        String choice = view.getInput("Enter your choice: ");
        switch (choice) {
            case "1":
                displayItems("Grocery");
                break;
            case "2":
                displayItems("Devices");
                break;
            case "3":
                displayItems("Fashion");
                break;
            case "4":
                displayItems("Dessert");
                break;
            default:
                view.displayMessage("Invalid choice.");
                selectCategory();
                break;
        }
    }

    private void displayItems(String category) {
        List<Product> products = getProductsByCategory(category);
        if (products.isEmpty()) {
            view.displayMessage("No products available in this category.");
            return;
        }

        view.displayMessage("Available products:");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            view.displayMessage((i + 1) + ". " + product.getName() + " - $" + product.getPrice());
        }

        String choice = view.getInput("Enter the number of the product to add to cart, or '0' to finish: ");
        if (!choice.equals("0")) {
            try {
                int productIndex = Integer.parseInt(choice) - 1;
                if (productIndex >= 0 && productIndex < products.size()) {
                    addToCart(products.get(productIndex));
                } else {
                    view.displayMessage("Invalid product number.");
                }
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input.");
            }
            displayItems(category);
        } else {
            proceedToPayment();
        }
    }

    private List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        try {
            String query = "SELECT * FROM products WHERE category = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(rs.getString("id"), rs.getString("name"), rs.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void addToCart(Product product) {
        cart.add(product);
        view.displayMessage("Added " + product.getName() + " to cart.");
    }

    // ... existing code ...
// ... existing code ...

private void proceedToPayment() {
    if (cart.isEmpty()) {
        view.displayMessage("Your cart is empty.");
        return;
    }

    // Show cart contents first
    viewCart();
    
    // Allow cart modifications
    String modifyChoice = view.getInput("Do you want to modify your cart? (yes/no): ");
    if (modifyChoice.equalsIgnoreCase("yes")) {
        modifyCart();
        // Check if cart is empty after modifications
        if (cart.isEmpty()) {
            view.displayMessage("Your cart is now empty.");
            return;
        }
        // Show updated cart
        viewCart();
    }
    
    double totalAmount = cart.stream().mapToDouble(Product::getPrice).sum();
    view.displayMessage("Your total amount is: $" + totalAmount);
    
    String continueChoice = view.getInput("Continue shopping? (yes/no): ");
    if (continueChoice.equalsIgnoreCase("yes")) {
        selectCategory(); // Go back to category selection
        return; // Exit this method to avoid proceeding to payment yet
    }
    
    String choice = view.getInput("Do you want to proceed to payment? (yes/no): ");
    if (choice.equalsIgnoreCase("yes")) {
        view.displayMessage("Payment successful! Thank you for your purchase.");
        cart.clear(); // Clear the cart after payment
    } else {
        view.displayMessage("Payment cancelled.");
    }
}

private void viewCart() {
    view.displayMessage("\n--- Your Cart ---");
    
    // Group items by product to show quantity
    // Using a Map: productId -> [product, quantity]
    Map<String, Object[]> cartItems = new HashMap<>();
    
    for (Product product : cart) {
        String productId = product.getId();
        if (cartItems.containsKey(productId)) {
            Object[] item = cartItems.get(productId);
            int quantity = (int) item[1];
            item[1] = quantity + 1;
        } else {
            cartItems.put(productId, new Object[]{product, 1});
        }
    }
    
    int index = 1;
    for (Object[] item : cartItems.values()) {
        Product product = (Product) item[0];
        int quantity = (int) item[1];
        double itemTotal = product.getPrice() * quantity;
        view.displayMessage(index + ". " + product.getName() + " - $" + product.getPrice() + 
                           " x " + quantity + " = $" + itemTotal);
        index++;
    }
    
    double totalAmount = cart.stream().mapToDouble(Product::getPrice).sum();
    view.displayMessage("Total: $" + totalAmount + "\n");
}

private void modifyCart() {
    // Create a temporary map to track items by index for the user interface
    Map<Integer, Object[]> indexedCart = new HashMap<>();
    Map<String, Object[]> cartItems = new HashMap<>();
    
    // Group items by product
    for (Product product : cart) {
        String productId = product.getId();
        if (cartItems.containsKey(productId)) {
            Object[] item = cartItems.get(productId);
            int quantity = (int) item[1];
            item[1] = quantity + 1;
        } else {
            cartItems.put(productId, new Object[]{product, 1});
        }
    }
    
    // Display indexed cart
    int index = 1;
    for (Object[] item : cartItems.values()) {
        Product product = (Product) item[0];
        int quantity = (int) item[1];
        double itemTotal = product.getPrice() * quantity;
        view.displayMessage(index + ". " + product.getName() + " - $" + product.getPrice() + 
                           " x " + quantity + " = $" + itemTotal);
        indexedCart.put(index, new Object[]{product, quantity});
        index++;
    }
    
    String itemChoice = view.getInput("Enter the number of the item to modify (0 to finish): ");
    if (itemChoice.equals("0")) {
        return;
    }
    
    try {
        int itemIndex = Integer.parseInt(itemChoice);
        if (!indexedCart.containsKey(itemIndex)) {
            view.displayMessage("Invalid item number.");
            modifyCart();
            return;
        }
        
        Object[] selectedItem = indexedCart.get(itemIndex);
        Product product = (Product) selectedItem[0];
        int currentQuantity = (int) selectedItem[1];
        
        view.displayMessage("Current quantity of " + product.getName() + ": " + currentQuantity);
        String action = view.getInput("Do you want to (1) Change quantity or (2) Remove item? ");
        
        if (action.equals("1")) {
            String newQuantityStr = view.getInput("Enter new quantity (0 to remove): ");
            try {
                int newQuantity = Integer.parseInt(newQuantityStr);
                if (newQuantity < 0) {
                    view.displayMessage("Invalid quantity.");
                } else if (newQuantity == 0) {
                    // Remove item completely
                    removeItemFromCart(product);
                    view.displayMessage(product.getName() + " removed from cart.");
                } else {
                    // Update quantity
                    updateItemQuantity(product, newQuantity);
                    view.displayMessage("Quantity updated to " + newQuantity);
                }
            } catch (NumberFormatException e) {
                view.displayMessage("Invalid input.");
            }
        } else if (action.equals("2")) {
            // Remove item completely
            removeItemFromCart(product);
            view.displayMessage(product.getName() + " removed from cart.");
        } else {
            view.displayMessage("Invalid choice.");
        }
        
        // Allow further modifications
        if (!cart.isEmpty()) {
            String continueModify = view.getInput("Modify another item? (yes/no): ");
            if (continueModify.equalsIgnoreCase("yes")) {
                modifyCart();
            }
        }
    } catch (NumberFormatException e) {
        view.displayMessage("Invalid input.");
        modifyCart();
    }
}

private void removeItemFromCart(Product product) {
    cart.removeIf(p -> p.getId().equals(product.getId()));
}

private void updateItemQuantity(Product product, int newQuantity) {
    // Remove all instances of this product
    List<Product> productsToRemove = cart.stream()
            .filter(p -> p.getId().equals(product.getId()))
            .collect(Collectors.toList());
    cart.removeAll(productsToRemove);
    
    // Add back the desired quantity
    for (int i = 0; i < newQuantity; i++) {
        cart.add(product);
    }
}



}