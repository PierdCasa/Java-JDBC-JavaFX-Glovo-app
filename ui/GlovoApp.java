package ui;

import database.DatabaseSeeder;
import database.DatabaseService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Customer;
import models.DeliveryMan;
import models.Order;
import models.OrderItem;
import models.Restaurant;

import java.util.ArrayList;

public class GlovoApp extends Application implements interfaces.IWeatherObserver {

    private Stage primaryStage;
    private Customer loggedInCustomer;
    private DeliveryMan loggedInDeliveryMan;
    private Restaurant loggedInRestaurant;
    private Order currentCart;
    private boolean isRainy = false;
    private services.App appFacade;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Glovo - Delivery App");
        
        // Ensure Database is seeded
        DatabaseSeeder.seedIfNeeded();
        
        // Initialize Service Layer Facade
        this.appFacade = new services.App();

        // Weather Observer Pattern
        services.WeatherService.getInstance().addObserver(this);
        services.WeatherService.getInstance().generateWeather();

        showLoginView();
        primaryStage.show();
    }

    @Override
    public void onWeatherChanged(boolean isRainy) {
        this.isRainy = isRainy;
        System.out.println("[GlovoApp] Weather updated. Is Rainy: " + isRainy);
    }
    
    public boolean isRainy() {
        return isRainy;
    }

    public void showLoginView() {
        LoginView loginView = new LoginView(this);
        Scene scene = new Scene(loginView.getView(), 400, 500);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showDashboard() {
        CustomerDashboardView dash = new CustomerDashboardView(this);
        Scene scene = new Scene(dash.getView(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showDeliveryManDashboard() {
        DeliveryManDashboardView dash = new DeliveryManDashboardView(this);
        Scene scene = new Scene(dash.getView(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showOrderHistory() {
        OrderHistoryView history = new OrderHistoryView(this);
        Scene scene = new Scene(history.getView(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public void showRestaurantMenu(Restaurant restaurant) {
        // Initialize an empty cart if visiting a new restaurant
        if (currentCart == null || currentCart.getRestaurant() == null || currentCart.getRestaurant().getRestaurantId() != restaurant.getRestaurantId()) {
            double deliveryFee = isRainy ? 15.0 : 10.0; // Higher fee if rainy
            currentCart = new Order(loggedInCustomer, restaurant, new ArrayList<>(), deliveryFee, 0.0);
        }

        RestaurantMenuView menu = new RestaurantMenuView(this, restaurant);
        Scene scene = new Scene(menu.getView(), 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public void showCart() {
        CartView cartView = new CartView(this);
        Scene scene = new Scene(cartView.getView(), 600, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    // Getters / Setters
    public services.App getAppFacade() { return appFacade; }
    
    public Customer getLoggedInCustomer() { return loggedInCustomer; }
    public void setLoggedInCustomer(Customer loggedInCustomer) { 
        this.loggedInCustomer = loggedInCustomer; 
        appFacade.getUserService().setLoggedInCustomer(loggedInCustomer);
    }
    
    public DeliveryMan getLoggedInDeliveryMan() { return loggedInDeliveryMan; }
    public void setLoggedInDeliveryMan(DeliveryMan loggedInDeliveryMan) { this.loggedInDeliveryMan = loggedInDeliveryMan; }
    
    public Restaurant getLoggedInRestaurant() { return loggedInRestaurant; }
    public void setLoggedInRestaurant(Restaurant loggedInRestaurant) { this.loggedInRestaurant = loggedInRestaurant; }
    
    public void showRestaurantDashboard() {
        RestaurantDashboardView dash = new RestaurantDashboardView(this);
        Scene scene = new Scene(dash.getView(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    
    public Order getCurrentCart() { return currentCart; }
    public void setCurrentCart(Order currentCart) { this.currentCart = currentCart; }
    
    public void addToCart(OrderItem item) {
        if (currentCart != null) {
            java.util.List<OrderItem> items = currentCart.getOrderItems();
            boolean found = false;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getProduct().getProductId() == item.getProduct().getProductId()) {
                    OrderItem existing = items.get(i);
                    OrderItem merged = new OrderItem(existing.getProduct(), existing.getQuantity() + item.getQuantity(), existing.getSelectedCustomizations());
                    items.set(i, merged);
                    found = true;
                    break;
                }
            }
            if (!found) {
                items.add(item);
            }
            currentCart = new Order(currentCart.getCustomer(), currentCart.getRestaurant(), items, currentCart.getDeliveryFee(), currentCart.getTipAmount());
        }
    }
    public void clearCart() {
        this.currentCart = null;
    }

    @Override
    public void stop() {
        DatabaseService.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
