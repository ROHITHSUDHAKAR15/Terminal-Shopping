# ShopEase Console

A Java-based command-line shopping system that demonstrates object-oriented design, modular architecture, and classic design patterns for a robust e-commerce simulation.

## Features

- **Product Management:** Browse, add, and manage products using a flexible product factory and prototype system.
- **Order Processing:** Build and place orders with a streamlined builder pattern, supporting multiple customers and products.
- **User Authentication:** Secure login and user management via a dedicated service layer.
- **Command-Line Interface:** Intuitive CLI for seamless user interaction and system navigation.
- **Extensible Architecture:** Utilizes MVC principles and design patterns (Factory, Builder, Prototype) for easy maintenance and scalability.

## Project Structure

```
src/
  controllers/      # Handles user actions and business logic
  database/         # Database connection and persistence logic
  models/           # Core data models: Customer, Order, Product
  services/         # User authentication and business services
  utils/            # Design pattern implementations (Factory, Builder, Prototype)
  views/            # Command-line interface (CLI) components
```

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd Java_Finale
   ```

2. **Compile the project:**
   ```bash
   javac -d bin src/**/*.java
   ```

3. **Run the application:**
   ```bash
   java -cp bin Main
   ```

## Technologies Used

- Java (OOP)
- MVC Architecture
- Design Patterns: Factory, Builder, Prototype
- Command-Line Interface (CLI)

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is for educational purposes and is not licensed for commercial use. 