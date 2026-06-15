package ui;

import database.DatabaseService;
import enums.OrderStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Order;
import models.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantDashboardView {

    private final GlovoApp app;
    private BorderPane root;

    public RestaurantDashboardView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();

        // Navbar
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Glovo - Restaurant Dashboard");
        title.getStyleClass().add("heading-2");
        title.setStyle("-fx-text-fill: #FFC244;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcomeLabel = new Label("Welcome, " + app.getLoggedInRestaurant().getName());
        welcomeLabel.getStyleClass().add("text-bold");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> {
            app.getAppFacade().logout();
            app.showLoginView();
        });

        navbar.getChildren().addAll(title, spacer, welcomeLabel, logoutBtn);
        root.setTop(navbar);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label sectionTitle = new Label("Active Orders");
        sectionTitle.getStyleClass().add("heading-2");
        
        List<Order> allOrders = DatabaseService.getInstance().getOrderDAO().findAll();
        List<Order> restaurantOrders = allOrders.stream()
                .filter(o -> o.getRestaurant().getRestaurantId() == app.getLoggedInRestaurant().getRestaurantId())
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .sorted((o1, o2) -> Integer.compare(o2.getOrderId(), o1.getOrderId()))
                .collect(Collectors.toList());

        if (restaurantOrders.isEmpty()) {
            Label emptyLabel = new Label("No active orders at the moment.");
            emptyLabel.getStyleClass().add("text-normal");
            content.getChildren().addAll(sectionTitle, emptyLabel);
        } else {
            content.getChildren().add(sectionTitle);
            for (Order order : restaurantOrders) {
                content.getChildren().add(createOrderCard(order, content));
            }
        }

        // Revenue Section
        Label revenueTitle = new Label("Financials");
        revenueTitle.getStyleClass().add("heading-2");
        revenueTitle.setPadding(new Insets(20, 0, 5, 0));
        
        VBox revenueCard = new VBox(5);
        revenueCard.getStyleClass().add("card");
        Label balanceLabel = new Label("Total Wallet Balance: " + String.format("%.2f RON", app.getLoggedInRestaurant().getWallet().getBalance()));
        balanceLabel.getStyleClass().add("text-bold");
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        revenueCard.getChildren().add(balanceLabel);

        // Reviews Section
        Label reviewsTitle = new Label("Customer Reviews - Average Rating: " + String.format("%.1f", app.getLoggedInRestaurant().getRating()) + " / 5");
        reviewsTitle.getStyleClass().add("heading-2");
        reviewsTitle.setPadding(new Insets(20, 0, 5, 0));

        VBox reviewsContainer = new VBox(10);
        List<models.Review> reviews = app.getLoggedInRestaurant().getReviews();
        if (reviews == null || reviews.isEmpty()) {
            Label noReviewsLabel = new Label("You have no reviews yet.");
            noReviewsLabel.getStyleClass().add("text-normal");
            reviewsContainer.getChildren().add(noReviewsLabel);
        } else {
            for (models.Review r : reviews) {
                VBox reviewCard = new VBox(5);
                reviewCard.getStyleClass().add("card");
                
                HBox header = new HBox(10);
                Label customerName = new Label(r.getCustomer().getFirstName() + " " + r.getCustomer().getSecondName());
                customerName.getStyleClass().add("text-bold");
                
                Region spacer2 = new Region();
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                
                Label ratingLabel = new Label(r.getRating() + "/5 Stars");
                ratingLabel.setStyle("-fx-text-fill: #FFC244; -fx-font-weight: bold;");
                
                header.getChildren().addAll(customerName, spacer2, ratingLabel);
                
                Label dateLabel = new Label(r.getDate().toLocalDate().toString());
                dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575;");
                
                Label commentLabel = new Label(r.getComment() != null ? r.getComment() : "");
                commentLabel.setWrapText(true);
                
                reviewCard.getChildren().addAll(header, dateLabel, commentLabel);
                reviewsContainer.getChildren().add(reviewCard);
            }
        }

        content.getChildren().addAll(revenueTitle, revenueCard, reviewsTitle, reviewsContainer);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setCenter(scrollPane);
    }

    private VBox createOrderCard(Order order, VBox parentContent) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label idLabel = new Label("Order #" + order.getOrderId());
        idLabel.getStyleClass().add("heading-2");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatus().toString());
        statusLabel.getStyleClass().add("text-bold");
        statusLabel.setStyle("-fx-text-fill: #FFC244;");

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        Label customerLabel = new Label("Customer: " + order.getCustomer().getFirstName() + " " + order.getCustomer().getSecondName());
        customerLabel.getStyleClass().add("text-bold");

        // Items list
        VBox itemsBox = new VBox(5);
        for (OrderItem item : order.getOrderItems()) {
            Label itemLabel = new Label(item.getQuantity() + "x " + item.getProduct().getName());
            itemLabel.getStyleClass().add("text-normal");
            itemsBox.getChildren().add(itemLabel);
        }

        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        if (order.getStatus() == OrderStatus.PENDING) {
            Button acceptBtn = new Button("Accept Order");
            acceptBtn.getStyleClass().add("btn-primary");
            acceptBtn.setOnAction(e -> {
                app.getAppFacade().getOrderService().updateOrderStatus(order, OrderStatus.ACCEPTED);
                app.showRestaurantDashboard(); // Refresh
            });
            actionsBox.getChildren().add(acceptBtn);
        } else if (order.getStatus() == OrderStatus.ACCEPTED) {
            Button prepareBtn = new Button("Start Preparing");
            prepareBtn.getStyleClass().add("btn-primary");
            prepareBtn.setOnAction(e -> {
                app.getAppFacade().getOrderService().updateOrderStatus(order, OrderStatus.PREPARING);
                app.showRestaurantDashboard(); // Refresh
            });
            actionsBox.getChildren().add(prepareBtn);
        } else if (order.getStatus() == OrderStatus.PREPARING) {
            Button readyBtn = new Button("Ready for Pickup");
            readyBtn.getStyleClass().add("btn-primary");
            readyBtn.setOnAction(e -> {
                app.getAppFacade().getOrderService().updateOrderStatus(order, OrderStatus.PICKED_UP);
                if (order.getDeliveryMan() != null && order.getRestaurant() != null) {
                    models.Location restLoc = order.getRestaurant().getLocationCoords();
                    if (restLoc != null) {
                        models.Location newLoc = new models.Location(restLoc.getX(), restLoc.getY(), "At " + order.getRestaurant().getName());
                        database.LocationDAO.getInstance().save(newLoc);
                        order.getDeliveryMan().setCurrentLocation(newLoc);
                        DatabaseService.getInstance().getUserDAO().update(order.getDeliveryMan());
                    }
                }
                app.showRestaurantDashboard(); // Refresh
            });
            actionsBox.getChildren().add(readyBtn);
        }

        card.getChildren().addAll(header, customerLabel, itemsBox, actionsBox);
        return card;
    }

    public BorderPane getView() {
        return root;
    }
}
