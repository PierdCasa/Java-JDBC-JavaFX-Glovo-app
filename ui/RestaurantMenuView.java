package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import models.OrderItem;
import models.Product;
import models.Restaurant;

public class RestaurantMenuView {

    private final GlovoApp app;
    private final Restaurant restaurant;
    private BorderPane root;

    public RestaurantMenuView(GlovoApp app, Restaurant restaurant) {
        this.app = app;
        this.restaurant = restaurant;
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

        Label title = new Label(restaurant.getName() + " - Menu");
        title.getStyleClass().add("heading-2");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button cartBtn = new Button("Cart");
        cartBtn.getStyleClass().add("btn-primary");
        cartBtn.setOnAction(e -> app.showCart());

        navbar.getChildren().addAll(backBtn, title, spacer, cartBtn);
        root.setTop(navbar);

        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (restaurant.getProducts().isEmpty()) {
            content.getChildren().add(new Label("No products available."));
        } else {
            for (Product p : restaurant.getProducts()) {
                content.getChildren().add(createProductCard(p));
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(5);
        Label name = new Label(product.getName());
        name.getStyleClass().add("text-bold");
        name.setStyle("-fx-font-size: 16px;");

        Label desc = new Label(product.getDescription());
        desc.getStyleClass().add("text-normal");
        desc.setWrapText(true);

        Label price = new Label(String.format("%.2f RON", product.getPrice()));
        price.getStyleClass().add("text-bold");
        price.setStyle("-fx-text-fill: #FFC244;");

        details.getChildren().addAll(name, desc, price);
        HBox.setHgrow(details, Priority.ALWAYS);

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("btn-secondary");
        addBtn.setOnAction(e -> {
            if (product.hasCustomizations()) {
                showCustomizationDialog(product);
            } else {
                OrderItem item = new OrderItem(product, 1);
                app.addToCart(item);
                System.out.println("Added " + product.getName() + " to cart!");
            }
        });

        card.getChildren().addAll(details, addBtn);
        return card;
    }

    private void showCustomizationDialog(Product product) {
        javafx.scene.control.Dialog<models.OrderItem> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Customize " + product.getName());
        dialog.setHeaderText("Select your ingredients:");

        javafx.scene.control.ButtonType addButtonType = new javafx.scene.control.ButtonType("Add to Cart", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, javafx.scene.control.ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        if (!product.getLockedIngredients().isEmpty()) {
            Label lockedLabel = new Label("Included Ingredients:");
            lockedLabel.setStyle("-fx-font-weight: bold;");
            vbox.getChildren().add(lockedLabel);
            for (models.Ingredient ing : product.getLockedIngredients()) {
                Label ingLabel = new Label("• " + ing.getName());
                vbox.getChildren().add(ingLabel);
            }
        }

        java.util.List<javafx.scene.control.CheckBox> checkBoxes = new java.util.ArrayList<>();
        if (!product.getOptionalIngredients().isEmpty()) {
            Label optionalLabel = new Label("Optional Extras:");
            optionalLabel.setStyle("-fx-font-weight: bold;");
            optionalLabel.setPadding(new Insets(10, 0, 0, 0));
            vbox.getChildren().add(optionalLabel);
            for (models.Ingredient ing : product.getOptionalIngredients()) {
                javafx.scene.control.CheckBox cb = new javafx.scene.control.CheckBox(ing.getName() + " (+" + String.format("%.2f", ing.getExtraPrice()) + " RON)");
                cb.setUserData(ing);
                checkBoxes.add(cb);
                vbox.getChildren().add(cb);
            }
        }

        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                java.util.List<models.Ingredient> selected = new java.util.ArrayList<>();
                for (javafx.scene.control.CheckBox cb : checkBoxes) {
                    if (cb.isSelected()) {
                        selected.add((models.Ingredient) cb.getUserData());
                    }
                }
                return new models.OrderItem(product, 1, selected);
            }
            return null;
        });

        java.util.Optional<models.OrderItem> result = dialog.showAndWait();
        result.ifPresent(item -> {
            app.addToCart(item);
            System.out.println("Added " + product.getName() + " with customizations to cart!");
        });
    }

    public BorderPane getView() {
        return root;
    }
}
