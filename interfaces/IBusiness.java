package interfaces;

import java.util.List;

import models.Product;
import models.Review;

public interface IBusiness {
    
    public String getName();

    public void setName(String name);

    public String getLocation();

    public void setLocation(String location);

    public double getRating() ;
    
    public void setRating(double rating);

    public List<Product> getProducts();

    public void addProduct(Product product) ;
    public void removeProduct(Product product) ;

    //Verif. daca produsul apartine business-ului
    public boolean hasProduct(Product product);

    // Review system
    public void addReview(Review review);
    public List<Review> getReviews();
}
