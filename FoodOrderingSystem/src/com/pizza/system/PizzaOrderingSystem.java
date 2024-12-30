package com.pizza.system;

import java.util.*;

// Enums for Pizza Customization
enum Crust { THIN, THICK, STUFFED }
enum Sauce { TOMATO, GARLIC, PESTO }
enum Topping { PEPPERONI, MUSHROOMS, ONIONS, EXTRA_CHEESE, OLIVES }

// ------------------------------------------
// Pizza Builder Pattern
// ------------------------------------------
class Pizza {
    protected Crust crust;
    protected Sauce sauce; 
    private List<Topping> toppings = new ArrayList<>();
    private String name;
    private double price;  

    protected Pizza(Builder builder) {
        this.crust = builder.crust; 
        this.sauce = builder.sauce;
        this.toppings = builder.toppings;
        this.name = builder.name;
        this.price = builder.price;
    }  

    public String getName() {
        return name;
    }
 
    public double getPrice() {
        return price;
    }

    // Builder class for Pizza customization
    public static class Builder {
        private Crust crust; 
        private Sauce sauce;
        private List<Topping> toppings = new ArrayList<>();
        private String name;
        private double price;

        public Builder crust(Crust crust) {
            this.crust = crust;
            return this;
        }

        public Builder sauce(Sauce sauce) {
            this.sauce = sauce;
            return this;
        }

        public Builder addTopping(Topping topping) {
            this.toppings.add(topping);
            return this;
        }

        public Builder toppings(List<Topping> toppings) {
            this.toppings.addAll(toppings);
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Pizza build() {
            return new Pizza(this);
        }
    }

    @Override
    public String toString() {
        return "Pizza{name='" + name + "', crust=" + crust + ", sauce=" + sauce + ", toppings=" + toppings + ", price=" + price + '}';
    }
}
// ------------------------------------------
// Decorator Pattern for Adding Features
// ------------------------------------------
abstract class PizzaDecorator extends Pizza {
    protected Pizza pizza;

    public PizzaDecorator(Pizza pizza) {
    	// Use the builder to replicate pizza's basic attributes
        super(new Builder().name(pizza.getName()).crust(pizza.crust).sauce(pizza.sauce)); 
        this.pizza = pizza;
    }

    @Override  
    public String toString() {
        return pizza.toString();
    }
}

// Additional features for pizza customization
class ExtraCheeseDecorator extends PizzaDecorator {
    public ExtraCheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String toString() {
        return pizza.toString() + " + Extra Cheese";
    }
}

class SpecialPackagingDecorator extends PizzaDecorator {
    public SpecialPackagingDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public String toString() {
        return pizza.toString() + " + Special Packaging";
    }
}

// ------------------------------------------
// User Profile and Favorite Pizzas
// ------------------------------------------


 class UserProfile {
    private String name;
    private List<Pizza> favoritePizzas = new ArrayList<>();
    private int loyaltyPoints;

    public UserProfile(String name) {
        this.name = name;
    }

    public void addFavoritePizza(Pizza pizza) {
        favoritePizzas.add(pizza);
        loyaltyPoints += 10; // Increment loyalty points
    }

    public List<Pizza> getFavoritePizzas() {
        return favoritePizzas;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Pizza reorderFavoritePizza(int index) {
        if (index >= 0 && index < favoritePizzas.size()) {
            return favoritePizzas.get(index);  // Reorder the pizza from favorites
        } else {
            System.out.println("Invalid selection. No such favorite pizza.");
            return null;
        }
    }

    public String getName() {
        return name; // Provide access to the user's name
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UserProfile{name='").append(name).append("', loyaltyPoints=").append(loyaltyPoints).append(", favoritePizzas=[");
        for (int i = 0; i < favoritePizzas.size(); i++) {
            sb.append("\n ").append(i + 1).append(". ").append(favoritePizzas.get(i));
        }
        sb.append("\n]}");
        return sb.toString();
    }
}

//------------------------------------------
//State Pattern for Order Tracking (Fixed)
//------------------------------------------
interface OrderState {
  void next(Order order);
  void prev(Order order);
  void printStatus();
}

class Order {
  private OrderState state = new PlacedState();
  private boolean isPickup; // true for pickup, false for delivery

  public Order(boolean isPickup) {
      this.isPickup = isPickup;
  }

  public void setState(OrderState state) {
      this.state = state;
  }

  public void next() {
      state.next(this);
  }

  public void prev() {
      state.prev(this);
  }

  public void printStatus() {
      state.printStatus();
  }

  public boolean isPickup() {
      return isPickup;
  }
}

//Define each state for order tracking
class PlacedState implements OrderState {
  public void next(Order order) {
      order.setState(new PreparingState());
  }

  public void prev(Order order) {
      System.out.println("Order is already in the initial state.");
  }

  public void printStatus() {
      System.out.println("Order placed and awaiting preparation.");
  }
}

class PreparingState implements OrderState {
  public void next(Order order) {
      if (order.isPickup()) {
          order.setState(new ReadyForPickupState());
      } else {
          order.setState(new OutForDeliveryState());
      }
  }

  public void prev(Order order) {
      order.setState(new PlacedState());
  }

  public void printStatus() {
      System.out.println("Order is being prepared.");
  }
}

class OutForDeliveryState implements OrderState {
  public void next(Order order) {
      order.setState(new DeliveredState());
  }

  public void prev(Order order) {
      order.setState(new PreparingState());
  }

  public void printStatus() {
      System.out.println("Order is out for delivery.");
  }
}

class ReadyForPickupState implements OrderState {
  public void next(Order order) {
      order.setState(new PickupCompletedState());
  }

  public void prev(Order order) {
      order.setState(new PreparingState());
  }

  public void printStatus() {
      System.out.println("Order is ready for pickup.");
  }
}

class DeliveredState implements OrderState {
  public void next(Order order) {
      order.setState(new InvoiceState());
  }

  public void prev(Order order) {
      order.setState(new OutForDeliveryState());
  }

  public void printStatus() {
      System.out.println("Order delivered successfully.");
  }
}

class PickupCompletedState implements OrderState {
  public void next(Order order) {
      order.setState(new InvoiceState());
  }

  public void prev(Order order) {
      order.setState(new ReadyForPickupState());
  }

  public void printStatus() {
      System.out.println("Pickup completed. Enjoy your meal!");
  }
}

class InvoiceState implements OrderState {
  public void next(Order order) {
      order.setState(new FeedbackState());
  }

  public void prev(Order order) {
      if (order.isPickup()) {
          order.setState(new PickupCompletedState());
      } else {
          order.setState(new DeliveredState());
      }
  }

  public void printStatus() {
      System.out.println("Invoice generated. Thank you for your order!");
  }
}

class FeedbackState implements OrderState {
  public void next(Order order) {
      System.out.println("Order process is complete. Thank you!");
  }

  public void prev(Order order) {
      order.setState(new InvoiceState());
  }

  public void printStatus() {
      System.out.println("Please provide feedback for your order.");
  }
}

// ------------------------------------------
// Command Pattern for User Actions
// ------------------------------------------
interface Command {
    void execute();
}

class PlaceOrderCommand implements Command {
    private Order order;

    public PlaceOrderCommand(Order order) {
        this.order = order;
    }

    public void execute() {
        order.printStatus();
    }
}

class FeedbackCommand implements Command {
    private String feedback;

    public FeedbackCommand(String feedback) {
        this.feedback = feedback;
    }

    public void execute() {
        System.out.println("Feedback received: " + feedback);
    }
}

// ------------------------------------------
// Chain of Responsibility Pattern for Customization
// ------------------------------------------
interface OrderCustomizationHandler {
    void setNextHandler(OrderCustomizationHandler handler);
    void handleRequest(String request);
}

class CrustHandler implements OrderCustomizationHandler {
    private OrderCustomizationHandler nextHandler;

    public void setNextHandler(OrderCustomizationHandler handler) {
        this.nextHandler = handler;
    }

    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("crust")) {
            System.out.println("Crust customization handled.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

class SauceHandler implements OrderCustomizationHandler {
    private OrderCustomizationHandler nextHandler;

    public void setNextHandler(OrderCustomizationHandler handler) {
        this.nextHandler = handler;
    }

    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("sauce")) {
            System.out.println("Sauce customization handled.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

class ToppingHandler implements OrderCustomizationHandler {
    private OrderCustomizationHandler nextHandler;

    public void setNextHandler(OrderCustomizationHandler handler) {
        this.nextHandler = handler;
    }

    public void handleRequest(String request) {
        if (request.equalsIgnoreCase("topping")) {
            System.out.println("Topping customization handled.");
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}

// ------------------------------------------
// Payment Strategy
// ------------------------------------------
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card.");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using PayPal.");
    }
}

// ------------------------------------------
// Observer Pattern for Notifications
// ------------------------------------------
interface Observer {
    void update(String status);
}

class Customer implements Observer {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    public void update(String status) {
        System.out.println(name + " notified: " + status);
    }
}

class OrderTracker {
    private List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String status) {
        for (Observer observer : observers) {
            observer.update(status);
        }
    }
}

//---------------------------------------------------------------------------------------------------------
                                            //Main Part
//---------------------------------------------------------------------------------------------------------

public class PizzaOrderingSystem {
 public static void main(String[] args) {
     // Initialize user profile and supporting objects
     UserProfile userProfile = new UserProfile("Jayan Perera");
     Scanner scanner = new Scanner(System.in);

     Order order = new Order(false);
     OrderTracker tracker = new OrderTracker();
     Customer customer = new Customer(userProfile.toString());
     tracker.addObserver(customer);

     System.out.println("=== Welcome to the Pizza Ordering System ===");

     while (true) {
         System.out.println("\nPlease choose an option:");
         System.out.println("1. Place an Order");
         System.out.println("2. View Profile");
         System.out.println("3. Exit");
         System.out.print("Your choice: ");

         int choice = scanner.nextInt();
         scanner.nextLine(); // Consume newline

         switch (choice) {
             case 1:
                 handleOrderPlacement(scanner, userProfile, order, tracker);
                 break;

             case 2:
                 System.out.println("\n=== Your Profile ===");
                 System.out.println("Name: " + userProfile.getName());
                 if (!userProfile.getFavoritePizzas().isEmpty()) {
                     System.out.println("\nFavorite Pizzas:");
                     for (Pizza pizza : userProfile.getFavoritePizzas()) {
                         System.out.println("- " + pizza);
                     }

                     System.out.println("\nWould you like to reorder a favorite pizza? (yes/no)");
                     System.out.print("Your choice: ");
                     String reorderChoice = scanner.nextLine();
                     if (reorderChoice.equalsIgnoreCase("yes")) {
                         System.out.println("Select a favorite pizza to reorder by number:");
                         for (int i = 0; i < userProfile.getFavoritePizzas().size(); i++) {
                             System.out.println((i + 1) + ". " + userProfile.getFavoritePizzas().get(i));
                         }
                         System.out.print("Your choice: ");
                         int pizzaChoice = scanner.nextInt();
                         scanner.nextLine(); // Consume newline
                         Pizza reorderedPizza = userProfile.reorderFavoritePizza(pizzaChoice - 1);
                         if (reorderedPizza != null) {
                             System.out.println("\nReordered Pizza: " + reorderedPizza);
                         }
                     }
                 }
                 break;

             case 3:
                 System.out.println("\nThank you for using the Pizza Ordering System! Goodbye!");
                 scanner.close();
                 System.exit(0);

             default:
                 System.out.println("\nInvalid choice. Please try again.");
         }
     }
 }

 private static void handleOrderPlacement(Scanner scanner, UserProfile userProfile, Order order, OrderTracker tracker) {
     System.out.println("\n=== Place Your Order ===");
     System.out.println("Is this order for:");
     System.out.println("1. Delivery");
     System.out.println("2. Take Away");
     System.out.print("Your choice: ");
     
     int orderType = scanner.nextInt();
     scanner.nextLine(); // Consume newline

     boolean isDelivery = orderType == 1;

     Pizza.Builder pizzaBuilder = new Pizza.Builder();
     System.out.println("\n--- Customize Your Pizza ---");

     // Handle Crust
     System.out.println("Choose crust:");
     for (Crust crust : Crust.values()) {
         System.out.println(crust.ordinal() + 1 + ". " + crust);
     }
     System.out.print("Your choice: ");
     int crustChoice = scanner.nextInt();
     scanner.nextLine();
     pizzaBuilder.crust(Crust.values()[crustChoice - 1]);

     // Handle Sauce
     System.out.println("\nChoose sauce:");
     for (Sauce sauce : Sauce.values()) {
         System.out.println(sauce.ordinal() + 1 + ". " + sauce);
     }
     System.out.print("Your choice: ");
     int sauceChoice = scanner.nextInt();
     scanner.nextLine();
     pizzaBuilder.sauce(Sauce.values()[sauceChoice - 1]);

     // Handle Toppings
     System.out.println("\nChoose toppings (type numbers separated by commas):");
     for (Topping topping : Topping.values()) {
         System.out.println(topping.ordinal() + 1 + ". " + topping + " (LKR 50.0)");
     }
     System.out.print("Your choice: ");
     String toppingInput = scanner.nextLine();
     List<Topping> selectedToppings = new ArrayList<>();
     for (String index : toppingInput.split(",")) {
         selectedToppings.add(Topping.values()[Integer.parseInt(index.trim()) - 1]);
     }
     pizzaBuilder.toppings(selectedToppings);

     // Finalize Pizza
     pizzaBuilder.name("Custom Pizza").price(1400.0 + selectedToppings.size() * 50.0);
     Pizza customPizza = pizzaBuilder.build();
     System.out.println("\nYour Pizza: " + customPizza);

     // Apply Seasonal Discount
     double discount = 0.10;
     double finalPrice = customPizza.getPrice() * (1 - discount);
     System.out.println("Seasonal Offer Applied! Final Price: LKR " + String.format("%.2f", finalPrice));

     // Add Extra Cheese
     boolean extraCheeseAdded = false;
     System.out.print("\nWould you like Extra Cheese for LKR 200.0? (yes/no): ");
     String extraCheeseChoice = scanner.nextLine();
     if (extraCheeseChoice.equalsIgnoreCase("yes")) {
         customPizza = new ExtraCheeseDecorator(customPizza);
         finalPrice += 200.0;
         extraCheeseAdded = true;
     }

     // Add Special Packaging
     boolean specialPackagingAdded = false;
     System.out.print("\nNeed Special Packaging for LKR 100.0? (yes/no): ");
     String specialPackagingChoice = scanner.nextLine();
     if (specialPackagingChoice.equalsIgnoreCase("yes")) {
         customPizza = new SpecialPackagingDecorator(customPizza);
         finalPrice += 100.0;
         specialPackagingAdded = true;
     }

     System.out.println("\n--- Final Total: LKR " + String.format("%.2f", finalPrice) + " ---");

     // Choose Payment Method
     System.out.println("How would you like to pay?");
     System.out.println("1. Credit Card");
     System.out.println("2. PayPal");

     int paymentChoice = scanner.nextInt();
     scanner.nextLine();
     PaymentStrategy paymentStrategy = paymentChoice == 1 ? new CreditCardPayment() : new PayPalPayment();
     paymentStrategy.pay(finalPrice);

     // Save Pizza to Profile
     System.out.print("\nSave this pizza as a favorite? (yes/no): ");
     String saveChoice = scanner.nextLine();
     if (saveChoice.equalsIgnoreCase("yes")) {
         userProfile.addFavoritePizza(customPizza);
         System.out.println("Pizza saved to favorites!");
     }

     // Handle Order Status
     processOrder(tracker, order, isDelivery, userProfile, customPizza, finalPrice, selectedToppings, extraCheeseAdded, specialPackagingAdded);
 }

 private static void processOrder(OrderTracker tracker, Order order, boolean isDelivery, UserProfile userProfile, Pizza customPizza, 
		 double finalPrice, List<Topping> selectedToppings, boolean extraCheeseAdded, boolean specialPackagingAdded) {
     System.out.println("\n");
     order.printStatus();
     order.next();
     System.out.println("\n");
     order.printStatus();
     order.next();
     if (isDelivery) {
         System.out.println("\n");
         order.printStatus();
         order.next();
         System.out.println("\n");
         order.printStatus();
         System.out.println("\nThank you for your order!");
     } else {
         System.out.println("\nYour order is ready for pickup.");
         System.out.println("\nYour order is picked up from the branch. Thank you!");
         
     }


     // Print Invoice
     printInvoice(userProfile.getName(), customPizza, finalPrice, selectedToppings, extraCheeseAdded, specialPackagingAdded);

     // Finalize rating
     System.out.println("\nPlease rate your experience (1-5): ");
     Scanner scanner = new Scanner(System.in);
     int rating = scanner.nextInt();
     scanner.nextLine();
     System.out.println("You rated us: " + rating + " stars. Thanks for your feedback!");

     // Add loyalty points
     userProfile.addFavoritePizza(customPizza);
     System.out.println("\nYou have earned " + userProfile.getLoyaltyPoints() + " points for this order!");
 }

 private static void printInvoice(String customerName, Pizza pizza, double finalPrice, List<Topping> selectedToppings, boolean extraCheeseAdded, boolean specialPackagingAdded) {
     System.out.println("\n____Your Invoice____");
     System.out.println("Customer: " + customerName);
     System.out.println("Base Price: LKR 1400.00");
     System.out.println("Toppings (" + selectedToppings.size() + "): LKR " + (selectedToppings.size() * 50.0));
     if (extraCheeseAdded) {
         System.out.println("Extra Cheese: LKR 200.00");
     }
     if (specialPackagingAdded) {
         System.out.println("Special Packaging: LKR 100.00");
     }
     System.out.println("Seasonal Offer: 10% Discount");
     System.out.println("Total Amount: LKR " + String.format("%.2f", finalPrice));
     System.out.println("=========================");
 }
}
