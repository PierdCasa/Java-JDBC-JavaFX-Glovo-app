package models;

import java.util.ArrayList;
import java.util.List;

import interfaces.IBusiness;

public class GroceryStore implements IBusiness {
    
    private int groceryStoreId;
    private String name;
    private String location;
    private double rating;
    private List<Product> products;
    private List<Review> reviews;
    private Wallet wallet;
    private Location locationCoords;

    public GroceryStore(String name, String location, double rating, Location locationCoords) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.products = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.wallet = new Wallet(0.0);
        this.locationCoords = locationCoords;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public int getGroceryStoreId() {
        return groceryStoreId;
    }

    public void setGroceryStoreId(int groceryStoreId) {
        this.groceryStoreId = groceryStoreId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void addProduct(Product product) {
        products.add(product);
        System.out.println("Produsul '"+product.getName()+"' a fost adaugat la magazinul "+name+".");
    }

    public void removeProduct(Product product) {
        if (products.remove(product)) {
            System.out.println("Produsul '"+product.getName()+"' a fost eliminat din magazinul "+name+".");
        } else {
            System.out.println("Produsul '"+product.getName()+"' nu a fost gasit in magazinul "+name+".");
        }
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public boolean hasProduct(Product product) {
        return products.contains(product);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Location getLocationCoords() {
        return locationCoords;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    //sistem de review uri 

    @Override
    public void addReview(Review review) {
        reviews.add(review);
        recalculateRating();
        System.out.println("  Review adaugat la '" + name + "' de " + review.getCustomer().getFirstName() 
            + " - " + review.getRating() + "/5. Rating nou: " + String.format("%.1f", rating));
    }

    public List<Review> getReviews() {
        return new ArrayList<>(reviews);
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
}
