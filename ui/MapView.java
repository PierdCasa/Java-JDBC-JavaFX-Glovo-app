package ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import models.Location;

public class MapView {
    
    public static Pane createMap(Location customerLoc, Location driverLoc, Location restaurantLoc) {
        Pane mapPane = new Pane();
        mapPane.setPrefSize(200, 200);
        mapPane.setMinSize(200, 200);
        mapPane.setMaxSize(200, 200);
        mapPane.setStyle("-fx-background-color: #E0E0E0; -fx-border-color: #BDBDBD; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        
        // Scale assumption: map coordinates from 0 to 50
        double maxCoord = 50.0;
        double paneSize = 200.0;
        
        // Fallback for customer location if database seeding missed it
        if (customerLoc == null) {
            customerLoc = new Location(10.0, 10.0, "Default Customer Loc");
        }
        
        if (customerLoc != null) {
            double cx = (customerLoc.getX() / maxCoord) * paneSize;
            double cy = (customerLoc.getY() / maxCoord) * paneSize;
            
            // Keep inside bounds
            cx = Math.max(10, Math.min(paneSize - 10, cx));
            cy = Math.max(10, Math.min(paneSize - 10, cy));
            
            Circle customerCircle = new Circle(cx, cy, 6, Color.BLUE);
            customerCircle.setStroke(Color.WHITE);
            customerCircle.setStrokeWidth(1.5);
            mapPane.getChildren().add(customerCircle);
        }
        
        if (driverLoc != null) {
            double dx = (driverLoc.getX() / maxCoord) * paneSize;
            double dy = (driverLoc.getY() / maxCoord) * paneSize;
            
            // Keep inside bounds
            dx = Math.max(10, Math.min(paneSize - 10, dx));
            dy = Math.max(10, Math.min(paneSize - 10, dy));
            
            Circle driverCircle = new Circle(dx, dy, 6, Color.RED);
            driverCircle.setStroke(Color.WHITE);
            driverCircle.setStrokeWidth(1.5);
            mapPane.getChildren().add(driverCircle);
        }
        
        if (restaurantLoc != null) {
            double rx = (restaurantLoc.getX() / maxCoord) * paneSize;
            double ry = (restaurantLoc.getY() / maxCoord) * paneSize;
            
            // Keep inside bounds
            rx = Math.max(10, Math.min(paneSize - 10, rx));
            ry = Math.max(10, Math.min(paneSize - 10, ry));
            
            Circle restaurantCircle = new Circle(rx, ry, 6, Color.ORANGE);
            restaurantCircle.setStroke(Color.WHITE);
            restaurantCircle.setStrokeWidth(1.5);
            mapPane.getChildren().add(restaurantCircle);
        }
        
        return mapPane;
    }
}
