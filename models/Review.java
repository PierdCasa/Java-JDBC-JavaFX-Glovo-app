package models;

import java.time.LocalDateTime;

public class Review {
    private static int idCounter = 0;

    private int reviewId;
    private Customer customer;
    private int rating;         // 1-5
    private String comment;
    private LocalDateTime date;
    private Order order;        // legatura la comanda livrata

    public Review(Customer customer, Order order, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating-ul trebuie sa fie intre 1 si 5.");
        }
        this.reviewId = ++idCounter;
        this.customer = customer;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + reviewId +
                ", client='" + customer.getFirstName() + " " + customer.getSecondName() + '\'' +
                ", rating=" + rating + "/5" +
                ", comentariu='" + comment + '\'' +
                ", data=" + date.toLocalDate() +
                '}';
    }
}
