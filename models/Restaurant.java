package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import interfaces.IBusiness;

public class Restaurant implements IBusiness {

    private int restaurantId;
    private String name;
    private String location;
    private double rating;
    private List<Product> products;
    private List<Review> reviews;
    private Wallet wallet;
    private Location locationCoords;

    public Restaurant(String name, String location, double rating, Location locationCoords) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.products = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.wallet = new Wallet(0.0);
        this.locationCoords = locationCoords;
    }

    public String getName() {
        return name;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }

    public Location getLocationCoords() {
        return locationCoords;
    }

    public void setLocationCoords(Location locationCoords) {
        this.locationCoords = locationCoords;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Product product) {
        products.add(product);
        System.out.println("Produsul '"+product.getName()+"' a fost adaugat la restaurantul "+name+".");
    }

    public void removeProduct(Product product) {
        if (products.remove(product)) {
            System.out.println("Produsul '"+product.getName()+"' a fost eliminat din restaurantul "+name+".");
        } else {
            System.out.println("Produsul '"+product.getName()+"' nu a fost gasit in restaurantul "+name+".");
        }
    }

    //Verif. daca produsul apartine restaurantului
    public boolean hasProduct(Product product) {
        return products.contains(product);
    }

    // --- Review System ---

    public void addReview(Review review) {
        reviews.add(review);
        recalculateRating();
        System.out.println("  Review adaugat la '" + name + "' de " + review.getCustomer().getFirstName() 
            + " - " + review.getRating() + "/5. Rating nou: " + String.format("%.1f", rating));
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        recalculateRating();
    }

    private double recalculateRating() {
        if (reviews.isEmpty()) {
            rating = 0.0;
            return rating;
        }
        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }
        rating = sum / reviews.size();
        return rating;
    }

    public void showReviews() {
        System.out.println("  === Review-uri pentru '" + name + "' (Rating: " + String.format("%.1f", rating) + "/5) ===");
        if (reviews.isEmpty()) {
            System.out.println("  Nu exista review-uri.");
            return;
        }
        for (Review r : reviews) {
            System.out.println("  " + r);
        }
    }

    // --- Revenue Reports ---

    public double getDailyRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return wallet.getRevenueForPeriod(startOfDay, LocalDateTime.now());
    }

    public double getWeeklyRevenue() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
        return wallet.getRevenueForPeriod(startOfWeek, LocalDateTime.now());
    }

    public double getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
        return wallet.getRevenueForPeriod(startOfMonth, LocalDateTime.now());
    }

    public void showRevenueReport() {
        System.out.println("  === Raport Venituri '" + name + "' ===");
        System.out.println("  Sold curent:     " + String.format("%.2f", wallet.getBalance()) + " RON");
        System.out.println("  Venit azi:       " + String.format("%.2f", getDailyRevenue()) + " RON");
        System.out.println("  Venit saptamana: " + String.format("%.2f", getWeeklyRevenue()) + " RON");
        System.out.println("  Venit luna:      " + String.format("%.2f", getMonthlyRevenue()) + " RON");
    }

    @Override
    public String toString() {
        return "Restaurant{" + "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + String.format("%.1f", rating) +
                ", numarProduse=" + products.size() +
                ", numarReview-uri=" + reviews.size() +
                ", wallet=" + wallet + '}';
    }
}
