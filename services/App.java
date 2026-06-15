package services;

import enums.VehicleType;
import interfaces.IDeliveryFeeStrategy;
import models.*;

import java.util.*;

/**
 * App - Facade Pattern
 * Punct central de acces care deleaga la servicii specializate:
 * UserService, OrderService, PaymentService, ReviewService.
 */
public class App {
    private UserService userService;
    private OrderService orderService;
    private PaymentService paymentService;
    private ReviewService reviewService;

    private Map<String, Restaurant> restaurants;
    private TreeSet<Category> sortedCategories;
    private List<Category> categories;
    private List<Product> allProducts;
    private Map<String, PromoCode> promoCodes;

    public App() {
        this.paymentService = new PaymentService();
        this.userService = new UserService();
        this.orderService = new OrderService(paymentService);
        this.reviewService = new ReviewService();

        this.restaurants = new HashMap<>();
        this.sortedCategories = new TreeSet<>();
        this.categories = new ArrayList<>();
        this.allProducts = new ArrayList<>();
        this.promoCodes = new HashMap<>();
    }

    // Servicii (getters)

    public UserService getUserService() {
        return userService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ReviewService getReviewService() {
        return reviewService;
    }

    // Categories 

    public Category addCategory(String name) {
        Category category = new Category(name);
        categories.add(category);
        sortedCategories.add(category);
        System.out.println("Categoria '" + name + "' a fost adaugata.");
        AuditService.getInstance().logAction("ADD_CATEGORY");
        return category;
    }

    public void showSortedCategories() {
        System.out.println("\n=== Categorii (sortate alfabetic) ===");
        int i = 1;
        for (Category cat : sortedCategories) {
            System.out.println(i + ". " + cat.getName());
            i++;
        }
    }

    // Products

    public Product addProduct(String name, Category category, String description, double price) {
        Product product = new Product(name, category, description, price);
        allProducts.add(product);
        AuditService.getInstance().logAction("ADD_PRODUCT");
        return product;
    }

    // Restaurants

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getName(), restaurant);
        System.out.println("Restaurantul '" + restaurant.getName() + "' a fost adaugat.");
        AuditService.getInstance().logAction("ADD_RESTAURANT");
    }

    public Restaurant getRestaurant(String name) {
        return restaurants.get(name);
    }

    public void showAllRestaurants() {
        System.out.println("\n=== Restaurante disponibile ===");
        int i = 1;
        for (Restaurant r : restaurants.values()) {
            System.out.println(i + ". " + r.getName() + " | Locatie: " + r.getLocation() 
                + " | Rating: " + String.format("%.1f", r.getRating())
                + " | Review-uri: " + r.getReviews().size());
            i++;
        }
    }

    public int getTotalRestaurants() {
        return restaurants.size();
    }

    // PromoCode

    public PromoCode addPromoCode(String code, double discountPercent, boolean freeDelivery, int maxUses) {
        PromoCode promo = new PromoCode(code, discountPercent, freeDelivery, maxUses);
        promoCodes.put(code.toUpperCase(), promo);
        System.out.println("Cod promotional '" + code + "' adaugat.");
        AuditService.getInstance().logAction("ADD_PROMO_CODE");
        return promo;
    }

    public PromoCode getPromoCode(String code) {
        return promoCodes.get(code.toUpperCase());
    }

    //Facade

    // User operations
    public Customer register(String firstName, String secondName, String phone, String email, String password, String deliveryAddress, Location location) {
        return userService.register(firstName, secondName, phone, email, password, deliveryAddress, location);
    }

    public DeliveryMan registerDeliveryMan(String firstName, String secondName, String phone, String email, String password, String licensePlate, VehicleType vehicleType, Location location) {
        return userService.registerDeliveryMan(firstName, secondName, phone, email, password, licensePlate, vehicleType, location);
    }

    public User login(String email, String password) {
        return userService.login(email, password);
    }

    public void logout() {
        userService.logout();
    }

    public Customer getLoggedInCustomer() {
        return userService.getLoggedInCustomer();
    }

    // Order operations
    public Order order(Restaurant restaurant, List<OrderItem> items, double tipAmount, PromoCode promoCode) {
        Customer loggedIn = userService.getLoggedInCustomer();
        DeliveryMan driver = userService.findAvailableDeliveryMan(restaurant.getLocationCoords());
        return orderService.placeOrder(loggedIn, restaurant, items, driver, tipAmount, promoCode);
    }

    public void checkStatus(Order order) {
        orderService.checkStatus(order);
    }

    public void showAllOrders() {
        orderService.showAllOrders();
    }

    public void showCustomerOrders(Customer customer) {
        orderService.showCustomerOrders(customer);
    }

    // Menu display
    public void getMenu(Restaurant restaurant) {
        if (restaurant == null) {
            System.out.println("Restaurantul nu exista!");
            return;
        }
        System.out.println("\n=== Meniu " + restaurant.getName() + " ===");
        List<Product> products = restaurant.getProducts();
        if (products.isEmpty()) {
            System.out.println("Meniul este gol.");
            return;
        }
        System.out.printf("%-5s %-25s %-15s %-30s %10s%n", "Nr.", "Produs", "Categorie", "Descriere", "Pret(RON)");
        System.out.println("-".repeat(90));
        int i = 1;
        for (Product p : products) {
            System.out.printf("%-5d %-25s %-15s %-30s %10.2f%n", i, p.getName(),
                    p.getCategory() != null ? p.getCategory().getName() : "N/A", p.getDescription(), p.getPrice());
            if (p.hasCustomizations()) {
                System.out.println("      ^ Personalizabil! (" + p.getOptionalIngredients().size() + " optiuni)");
            }
            i++;
        }
    }

    // Review operations
    public Review addReview(Customer customer, Order order, Restaurant restaurant, int rating, String comment) {
        return reviewService.addReview(customer, order, restaurant, rating, comment);
    }

    // Payment operations
    public void topUpWallet(User user, double amount) {
        paymentService.topUpWallet(user, amount);
    }

    // Delivery fee strategy
    public void setDeliveryFeeStrategy(IDeliveryFeeStrategy strategy) {
        orderService.setDeliveryFeeStrategy(strategy);
    }
}
