package ui;

import database.DatabaseService;
import enums.OrderStatus;
import enums.TransactionType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Order;
import models.OrderItem;
import models.Transaction;

import java.time.LocalDateTime;

public class CartView {

    private final GlovoApp app;
    private BorderPane root;

    public CartView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();

        // Navbar
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Menu");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> {
            if (app.getCurrentCart() != null && app.getCurrentCart().getRestaurant() != null) {
                app.showRestaurantMenu(app.getCurrentCart().getRestaurant());
            } else {
                app.showDashboard();
            }
        });

        Label title = new Label("Your Cart");
        title.getStyleClass().add("heading-2");

        navbar.getChildren().addAll(backBtn, title);
        root.setTop(navbar);

        Order cart = app.getCurrentCart();

        if (cart == null || cart.getOrderItems().isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty.");
            emptyLabel.getStyleClass().add("heading-2");
            root.setCenter(new StackPane(emptyLabel));
            return;
        }

        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        double totalItemsPrice = 0;
        for (OrderItem item : cart.getOrderItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            Label name = new Label(item.getQuantity() + "x " + item.getProduct().getName());
            name.getStyleClass().add("text-bold");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label price = new Label(String.format("%.2f RON", item.getPrice()));
            itemRow.getChildren().addAll(name, spacer, price);
            content.getChildren().add(itemRow);
            totalItemsPrice += item.getPrice();
        }

        double deliveryFee = cart.getDeliveryFee();
        double tipAmount = cart.getTipAmount();
        double total = totalItemsPrice + deliveryFee + tipAmount;
        
        // Tip selection
        VBox tipSelectionBox = new VBox(10);
        tipSelectionBox.setPadding(new Insets(10, 0, 10, 0));
        Label tipLabel = new Label("Select Tip Amount:");
        tipLabel.getStyleClass().add("text-bold");
        HBox togglesBox = new HBox(15);
        javafx.scene.control.ToggleGroup tipGroup = new javafx.scene.control.ToggleGroup();
        
        double[] tipOptions = {0.0, 5.0, 10.0, 15.0};
        for (double tipVal : tipOptions) {
            javafx.scene.control.RadioButton rb = new javafx.scene.control.RadioButton(String.format("%.0f RON", tipVal));
            rb.setToggleGroup(tipGroup);
            if (tipAmount == tipVal) {
                rb.setSelected(true);
            }
            rb.setOnAction(e -> {
                cart.setTipAmount(tipVal);
                app.showCart(); // Refresh view
            });
            togglesBox.getChildren().add(rb);
        }
        tipSelectionBox.getChildren().addAll(tipLabel, togglesBox);

        VBox summaryBox = new VBox(10);
        summaryBox.getStyleClass().add("card");
        summaryBox.setPadding(new Insets(15));
        
        HBox subtotalRow = createSummaryRow("Subtotal", totalItemsPrice);
        HBox deliveryRow = createSummaryRow("Delivery Fee", deliveryFee);
        HBox tipRow = createSummaryRow("Tip for Driver", tipAmount);
        HBox totalRow = createSummaryRow("Total", total);
        totalRow.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        summaryBox.getChildren().addAll(subtotalRow, deliveryRow, tipRow, totalRow);
        content.getChildren().addAll(new Region(), tipSelectionBox, summaryBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Footer Checkout
        VBox footer = new VBox();
        footer.setPadding(new Insets(20));
        footer.setAlignment(Pos.CENTER);
        
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        statusLabel.setVisible(false);

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.getStyleClass().add("btn-primary");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        
        double finalTotal = total;
        double finalTotalItemsPrice = totalItemsPrice;
        
        HBox topUpBox = new HBox(10);
        topUpBox.setAlignment(Pos.CENTER);
        topUpBox.setVisible(false);
        topUpBox.setManaged(false);
        javafx.scene.control.TextField topUpInput = new javafx.scene.control.TextField();
        topUpInput.setPromptText("Amount (RON)");
        Button topUpBtn = new Button("Top Up");
        topUpBtn.getStyleClass().add("btn-secondary");
        topUpBox.getChildren().addAll(topUpInput, topUpBtn);
        
        topUpBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(topUpInput.getText());
                if (amount > 0) {
                    app.getAppFacade().topUpWallet(app.getLoggedInCustomer(), amount);
                    statusLabel.setText("Top-Up successful! You can now checkout.");
                    statusLabel.setStyle("-fx-text-fill: green;");
                    topUpBox.setVisible(false);
                    topUpBox.setManaged(false);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid amount.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });

        checkoutBtn.setOnAction(e -> {
            if (app.getLoggedInCustomer().getWallet().getBalance() < finalTotal) {
                statusLabel.setText("Insufficient funds! Please top up.");
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setVisible(true);
                topUpBox.setVisible(true);
                topUpBox.setManaged(true);
                return;
            }

            // Checkout Logic using Service Layer
            services.App appFacade = app.getAppFacade();
            
            // Logged in customer must be set in Facade
            appFacade.getUserService().setLoggedInCustomer(app.getLoggedInCustomer());
            
            // Place order using App Facade which handles payment, DAO saves, and audit
            Order newOrder = appFacade.order(cart.getRestaurant(), cart.getOrderItems(), tipAmount, null);
            
            if (newOrder != null) {
                statusLabel.setText("Order placed successfully! ID: " + newOrder.getOrderId());
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setVisible(true);
    
                // Clear cart and wait a moment before going back
                app.clearCart();
                checkoutBtn.setDisable(true);
            } else {
                statusLabel.setText("Order failed.");
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setVisible(true);
            }
        });

        footer.getChildren().addAll(statusLabel, topUpBox, checkoutBtn);
        root.setBottom(footer);
    }

    private HBox createSummaryRow(String labelText, double value) {
        HBox row = new HBox();
        Label lbl = new Label(labelText);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(String.format("%.2f RON", value));
        row.getChildren().addAll(lbl, spacer, val);
        return row;
    }

    public BorderPane getView() {
        return root;
    }
}
