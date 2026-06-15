package services;

import enums.OrderStatus;
import models.*;

public class ReviewService {

    // Adauga un review la un restaurant (doar dupa livrare, de catre clientul comenzii)
    public Review addReview(Customer customer, Order order, Restaurant restaurant, int rating, String comment) {
        // Verif. 1: Comanda trebuie sa fie livrata
        if (order.getStatus() != OrderStatus.DELIVERED) {
            System.out.println("  ! Nu poti lasa un review - comanda #" + order.getOrderId() + " nu a fost inca livrata.");
            return null;
        }

        // Verif. 2: Clientul trebuie sa fie cel care a plasat comanda
        if (order.getCustomer().getUserId() != customer.getUserId()) {
            System.out.println("  ! Nu poti lasa un review - nu ai plasat comanda #" + order.getOrderId() + ".");
            return null;
        }

        // Verif. 3: Comanda trebuie sa fie la restaurantul respectiv
        if (!order.getRestaurant().getName().equals(restaurant.getName())) {
            System.out.println("  ! Comanda #" + order.getOrderId() + " nu a fost plasata la restaurantul '" + restaurant.getName() + "'.");
            return null;
        }

        Review review = new Review(customer, order, rating, comment);
        restaurant.addReview(review);
        
        // Save to DB
        database.DatabaseService.getInstance().getReviewDAO().save(review);
        database.DatabaseService.getInstance().getRestaurantDAO().update(restaurant);
        
        AuditService.getInstance().logAction("ADD_REVIEW");
        return review;
    }

    public void showRestaurantReviews(Restaurant restaurant) {
        restaurant.showReviews();
    }
}
