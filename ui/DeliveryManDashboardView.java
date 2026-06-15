package ui;

import database.DatabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.Order;
import models.OrderItem;

import java.util.List;

public class DeliveryManDashboardView {

    private final GlovoApp app;
    private BorderPane root;

    public DeliveryManDashboardView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();

        // Navbar
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Glovo - Driver Dashboard");
        title.getStyleClass().add("heading-2");
        title.setStyle("-fx-text-fill: #FFC244;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Display revenue (wallet balance)
        Label revenueLabel = new Label(String.format("Revenue: %.2f RON", app.getLoggedInDeliveryMan().getWallet().getBalance()));
        revenueLabel.getStyleClass().add("text-bold");
        revenueLabel.setStyle("-fx-text-fill: #4CAF50;");

        Label welcomeLabel = new Label("Hi, Driver " + app.getLoggedInDeliveryMan().getFirstName() + "!");
        welcomeLabel.getStyleClass().add("text-bold");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> {
            app.getAppFacade().logout();
            app.showLoginView();
        });

        navbar.getChildren().addAll(title, spacer, revenueLabel, welcomeLabel, logoutBtn);
        root.setTop(navbar);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        Label sectionTitle = new Label("Your Assigned Orders");
        sectionTitle.getStyleClass().add("heading-2");

        List<Order> orders = DatabaseService.getInstance().getOrderDAO().findByDeliveryManId(app.getLoggedInDeliveryMan().getUserId());

        if (orders == null || orders.isEmpty()) {
            Label emptyLabel = new Label("No assigned orders at the moment.");
            emptyLabel.getStyleClass().add("text-normal");
            content.getChildren().addAll(sectionTitle, emptyLabel);
        } else {
            // Sort to show newest first
            orders.sort((o1, o2) -> Integer.compare(o2.getOrderId(), o1.getOrderId()));
            content.getChildren().add(sectionTitle);
            for (Order order : orders) {
                content.getChildren().add(createOrderCard(order));
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setCenter(scrollPane);
    }

    private VBox createOrderCard(Order order) {
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
        if (order.getStatus().toString().equals("DELIVERED")) {
            statusLabel.setStyle("-fx-text-fill: green;");
        } else if (order.getStatus().toString().equals("CANCELLED")) {
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setStyle("-fx-text-fill: #FFC244;");
        }

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        Label customerLabel = new Label("Customer: " + order.getCustomer().getFirstName() + " " + order.getCustomer().getSecondName());
        customerLabel.getStyleClass().add("text-bold");

        Label restaurantLabel = new Label("Restaurant: " + order.getRestaurant().getName());
        restaurantLabel.getStyleClass().add("text-bold");

        String dateStr = order.getOrderDate() != null ? order.getOrderDate().toLocalDate().toString() : "Unknown date";
        Label dateLabel = new Label("Date: " + dateStr);
        dateLabel.getStyleClass().add("text-normal");

        Label payoutLabel = new Label(String.format("Delivery Payout: %.2f RON", order.getDeliveryFee() + order.getTipAmount()));
        payoutLabel.getStyleClass().add("text-bold");
        payoutLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");

        HBox contentBox = new HBox(20);
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(customerLabel, restaurantLabel, dateLabel, payoutLabel);
        contentBox.getChildren().add(infoBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        if (order.getStatus() != enums.OrderStatus.DELIVERED && order.getStatus() != enums.OrderStatus.CANCELLED) {
            models.Location driverLoc = app.getLoggedInDeliveryMan().getCurrentLocation();
            models.Location restaurantLoc = order.getRestaurant().getLocationCoords();
            Pane mapPane = MapView.createMap(order.getCustomer().getLocation(), driverLoc, restaurantLoc);
            
            VBox mapContainer = new VBox(5);
            Label mapLabel = new Label("Live Map (Blue=Customer, Red=You, Orange=Restaurant)");
            mapLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575;");
            mapContainer.getChildren().addAll(mapLabel, mapPane);
            
            contentBox.getChildren().add(mapContainer);
            
            // Add state transition buttons
            if (order.getStatus() == enums.OrderStatus.PICKED_UP) {
                Button arrivingBtn = new Button("Arrived at Customer");
                arrivingBtn.getStyleClass().add("btn-primary");
                arrivingBtn.setOnAction(e -> {
                    models.Location cLoc = order.getCustomer().getLocation();
                    if (cLoc != null) {
                        models.Location newLoc = new models.Location(cLoc.getX() + 0.1, cLoc.getY() + 0.1, "Arrived at Customer");
                        database.LocationDAO.getInstance().save(newLoc);
                        app.getLoggedInDeliveryMan().setCurrentLocation(newLoc);
                        DatabaseService.getInstance().getUserDAO().update(app.getLoggedInDeliveryMan());
                    }
                    app.getAppFacade().getOrderService().updateOrderStatus(order, enums.OrderStatus.ARRIVING);
                    app.showDeliveryManDashboard(); // Refresh
                });
                contentBox.getChildren().add(arrivingBtn);
            } else if (order.getStatus() == enums.OrderStatus.ARRIVING) {
                Button deliveredBtn = new Button("Delivered");
                deliveredBtn.getStyleClass().add("btn-primary");
                deliveredBtn.setOnAction(e -> {
                    app.getAppFacade().getOrderService().updateOrderStatus(order, enums.OrderStatus.DELIVERED);
                    app.showDeliveryManDashboard(); // Refresh
                });
                contentBox.getChildren().add(deliveredBtn);
            }
        }

        card.getChildren().addAll(header, contentBox);
        return card;
    }

    public BorderPane getView() {
        return root;
    }
}
