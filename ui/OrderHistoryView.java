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

public class OrderHistoryView {

    private final GlovoApp app;
    private BorderPane root;

    public OrderHistoryView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new BorderPane();

        // Navbar
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> app.showDashboard());

        Label title = new Label("Order History");
        title.getStyleClass().add("heading-2");

        navbar.getChildren().addAll(backBtn, title);
        root.setTop(navbar);

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        List<Order> orders = DatabaseService.getInstance().getOrderDAO().findByCustomerId(app.getLoggedInCustomer().getUserId());
        
        if (orders == null || orders.isEmpty()) {
            Label emptyLabel = new Label("You haven't placed any orders yet.");
            emptyLabel.getStyleClass().add("heading-2");
            content.getChildren().add(emptyLabel);
        } else {
            // Sort to show newest first (assuming higher ID = newer)
            orders.sort((o1, o2) -> Integer.compare(o2.getOrderId(), o1.getOrderId()));
            
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

        Label restaurantLabel = new Label("Restaurant: " + order.getRestaurant().getName());
        restaurantLabel.getStyleClass().add("text-bold");
        
        String dateStr = order.getOrderDate() != null ? order.getOrderDate().toLocalDate().toString() : "Unknown date";
        Label dateLabel = new Label("Date: " + dateStr);
        dateLabel.getStyleClass().add("text-normal");
        
        // Items list
        VBox itemsBox = new VBox(5);
        for (OrderItem item : order.getOrderItems()) {
            Label itemLabel = new Label(item.getQuantity() + "x " + item.getProduct().getName() + " - " + String.format("%.2f RON", item.getPrice()));
            itemLabel.getStyleClass().add("text-normal");
            itemsBox.getChildren().add(itemLabel);
        }

        Label totalLabel = new Label(String.format("Total: %.2f RON", order.getTotalPrice()));
        totalLabel.getStyleClass().add("text-bold");
        totalLabel.setStyle("-fx-font-size: 16px;");

        HBox contentBox = new HBox(20);
        contentBox.getChildren().add(itemsBox);
        HBox.setHgrow(itemsBox, Priority.ALWAYS);

        if (order.getStatus() != enums.OrderStatus.DELIVERED && order.getStatus() != enums.OrderStatus.CANCELLED) {
            models.Location driverLoc = (order.getDeliveryMan() != null) ? order.getDeliveryMan().getCurrentLocation() : null;
            models.Location restaurantLoc = order.getRestaurant().getLocationCoords();
            Pane mapPane = MapView.createMap(order.getCustomer().getLocation(), driverLoc, restaurantLoc);
            
            VBox mapContainer = new VBox(5);
            Label mapLabel = new Label("Live Map (Blue=You, Red=Driver, Orange=Restaurant)");
            mapLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #757575;");
            mapContainer.getChildren().addAll(mapLabel, mapPane);
            
            contentBox.getChildren().add(mapContainer);
        }

        card.getChildren().addAll(header, restaurantLabel, dateLabel, contentBox, totalLabel);

        if (order.getStatus() == enums.OrderStatus.DELIVERED) {
            models.Review existingReview = database.ReviewDAO.getInstance().findByOrderId(order.getOrderId());
            if (existingReview == null) {
                Button reviewBtn = new Button("Leave a Review");
                reviewBtn.getStyleClass().add("btn-primary");
                reviewBtn.setOnAction(e -> showReviewDialog(order));
                card.getChildren().add(reviewBtn);
            } else {
                Label reviewedLabel = new Label("You rated this: " + existingReview.getRating() + "/5");
                reviewedLabel.getStyleClass().add("text-bold");
                reviewedLabel.setStyle("-fx-text-fill: #4CAF50;");
                card.getChildren().add(reviewedLabel);
            }
        }

        return card;
    }

    private void showReviewDialog(Order order) {
        javafx.scene.control.Dialog<models.Review> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Review Order #" + order.getOrderId());
        dialog.setHeaderText("How was your experience with " + order.getRestaurant().getName() + "?");

        javafx.scene.control.ButtonType submitButtonType = new javafx.scene.control.ButtonType("Submit", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, javafx.scene.control.ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        Label ratingLabel = new Label("Rating (1-5):");
        javafx.scene.control.ComboBox<Integer> ratingBox = new javafx.scene.control.ComboBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setValue(5);

        Label commentLabel = new Label("Comment (Optional):");
        javafx.scene.control.TextArea commentArea = new javafx.scene.control.TextArea();
        commentArea.setPrefRowCount(3);

        vbox.getChildren().addAll(ratingLabel, ratingBox, commentLabel, commentArea);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return new models.Review(app.getLoggedInCustomer(), order, ratingBox.getValue(), commentArea.getText());
            }
            return null;
        });

        java.util.Optional<models.Review> result = dialog.showAndWait();
        result.ifPresent(reviewData -> {
            app.getAppFacade().addReview(
                app.getLoggedInCustomer(), 
                order, 
                order.getRestaurant(), 
                reviewData.getRating(), 
                reviewData.getComment()
            );
            
            app.showOrderHistory(); // Refresh to show the label
        });
    }

    public BorderPane getView() {
        return root;
    }
}
