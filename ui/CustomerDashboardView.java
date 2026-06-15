package ui;

import database.DatabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Restaurant;

import java.util.List;

public class CustomerDashboardView {

    private final GlovoApp app;
    private BorderPane root;

    public CustomerDashboardView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();

        // Navbar
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Glovo - Restaurants");
        title.getStyleClass().add("heading-2");
        title.setStyle("-fx-text-fill: #FFC244;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label balanceLabel = new Label(String.format("Balance: %.2f RON", app.getLoggedInCustomer().getWallet().getBalance()));
        balanceLabel.getStyleClass().add("text-bold");
        balanceLabel.setStyle("-fx-text-fill: #4CAF50;");

        HBox topUpBox = new HBox(10);
        topUpBox.setAlignment(Pos.CENTER);
        topUpBox.setVisible(false);
        topUpBox.setManaged(false);
        javafx.scene.control.TextField topUpInput = new javafx.scene.control.TextField();
        topUpInput.setPromptText("Amount (RON)");
        Button topUpSubmitBtn = new Button("Top Up");
        topUpSubmitBtn.getStyleClass().add("btn-primary");
        topUpBox.getChildren().addAll(topUpInput, topUpSubmitBtn);

        Button addFundsBtn = new Button("+ Add Funds");
        addFundsBtn.getStyleClass().add("btn-secondary");
        addFundsBtn.setOnAction(e -> {
            boolean isVisible = topUpBox.isVisible();
            topUpBox.setVisible(!isVisible);
            topUpBox.setManaged(!isVisible);
        });

        topUpSubmitBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(topUpInput.getText());
                if (amount > 0) {
                    app.getAppFacade().topUpWallet(app.getLoggedInCustomer(), amount);
                    balanceLabel.setText(String.format("Balance: %.2f RON", app.getLoggedInCustomer().getWallet().getBalance()));
                    topUpInput.clear();
                    topUpBox.setVisible(false);
                    topUpBox.setManaged(false);
                }
            } catch (NumberFormatException ex) {
                // Invalid amount
            }
        });

        Button historyBtn = new Button("Order History");
        historyBtn.getStyleClass().add("btn-secondary");
        historyBtn.setOnAction(e -> app.showOrderHistory());

        Label welcomeLabel = new Label("Hi, " + app.getLoggedInCustomer().getFirstName() + "!");
        welcomeLabel.getStyleClass().add("text-bold");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> {
            app.getAppFacade().logout();
            app.showLoginView();
        });

        navbar.getChildren().addAll(title, spacer, topUpBox, balanceLabel, addFundsBtn, historyBtn, welcomeLabel, logoutBtn);
        root.setTop(navbar);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label sectionTitle = new Label("What are you craving today?");
        sectionTitle.getStyleClass().add("heading-2");

        FlowPane restaurantGrid = new FlowPane(20, 20);
        List<Restaurant> restaurants = DatabaseService.getInstance().getRestaurantDAO().findAll();

        for (Restaurant r : restaurants) {
            VBox card = createRestaurantCard(r);
            restaurantGrid.getChildren().add(card);
        }

        if (app.isRainy()) {
            Label weatherWarning = new Label("☔ It's currently raining! Higher delivery fees apply.");
            weatherWarning.setStyle("-fx-text-fill: white; -fx-background-color: #2196F3; -fx-padding: 10px; -fx-background-radius: 5px; -fx-font-weight: bold;");
            weatherWarning.setMaxWidth(Double.MAX_VALUE);
            content.getChildren().add(weatherWarning);
        }

        content.getChildren().addAll(sectionTitle, restaurantGrid);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPrefWidth(200);

        Label name = new Label(restaurant.getName());
        name.getStyleClass().add("heading-2");

        Label rating = new Label("⭐ " + restaurant.getRating());
        rating.getStyleClass().add("text-bold");

        Label location = new Label(restaurant.getLocation());
        location.getStyleClass().add("text-normal");
        location.setWrapText(true);

        Button viewMenuBtn = new Button("View Menu");
        viewMenuBtn.getStyleClass().add("btn-primary");
        viewMenuBtn.setMaxWidth(Double.MAX_VALUE);
        viewMenuBtn.setOnAction(e -> app.showRestaurantMenu(restaurant));

        card.getChildren().addAll(name, rating, location, viewMenuBtn);
        return card;
    }

    public BorderPane getView() {
        return root;
    }
}
