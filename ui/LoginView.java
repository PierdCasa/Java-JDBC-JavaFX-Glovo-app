package ui;

import database.DatabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.Customer;
import models.User;

public class LoginView {

    private final GlovoApp app;
    private VBox root;

    public LoginView(GlovoApp app) {
        this.app = app;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("Glovo");
        title.getStyleClass().add("heading-1");
        title.setStyle("-fx-text-fill: #FFC244; -fx-font-size: 36px;");

        Label subtitle = new Label("Login to continue");
        subtitle.getStyleClass().add("text-normal");

        TextField emailField = new TextField();
        emailField.setPromptText("Email (e.g. maria@email.com)");
        emailField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (e.g. maria123)");
        passwordField.getStyleClass().add("text-field");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String pass = passwordField.getText();

            User user = app.getAppFacade().login(email, pass);
            if (user != null) {
                if (user instanceof Customer) {
                    app.setLoggedInCustomer((Customer) user);
                    app.showDashboard();
                } else if (user instanceof models.DeliveryMan) {
                    app.setLoggedInDeliveryMan((models.DeliveryMan) user);
                    app.showDeliveryManDashboard();
                } else {
                    errorLabel.setText("Unsupported user role!");
                    errorLabel.setVisible(true);
                }
            } else {
                errorLabel.setText("Invalid email or password!");
                errorLabel.setVisible(true);
            }
        });

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        
        Label restLoginLabel = new Label("Restaurant Owner?");
        restLoginLabel.getStyleClass().add("text-bold");
        
        ComboBox<models.Restaurant> restaurantComboBox = new ComboBox<>();
        // Set a string converter to show only the restaurant name
        restaurantComboBox.setConverter(new javafx.util.StringConverter<models.Restaurant>() {
            @Override
            public String toString(models.Restaurant r) {
                return r != null ? r.getName() : "";
            }
            @Override
            public models.Restaurant fromString(String string) {
                return null; // Not needed
            }
        });
        restaurantComboBox.getItems().addAll(DatabaseService.getInstance().getRestaurantDAO().findAll());
        restaurantComboBox.setPromptText("Select Restaurant");
        
        Button restLoginBtn = new Button("Login as Restaurant");
        restLoginBtn.getStyleClass().add("btn-secondary");
        restLoginBtn.setOnAction(e -> {
            models.Restaurant selected = restaurantComboBox.getValue();
            if (selected != null) {
                app.setLoggedInRestaurant(selected);
                services.AuditService.getInstance().logAction("LOGIN");
                app.showRestaurantDashboard();
            }
        });

        root.getChildren().addAll(title, subtitle, emailField, passwordField, loginBtn, errorLabel, sep, restLoginLabel, restaurantComboBox, restLoginBtn);
    }

    public VBox getView() {
        return root;
    }
}
