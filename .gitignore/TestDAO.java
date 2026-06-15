import database.*;
import models.*;

import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        System.out.println("=== Starting DAO Test ===");
        
        try {
            DatabaseService dbService = DatabaseService.getInstance();
            
            // 1. Create a Category
            System.out.println("\n[1] Testing Category DAO - Create");
            Category category = new Category("Test Category");
            dbService.getCategoryDAO().save(category);
            System.out.println("Category saved. ID is now: " + category.getCategoryId());
            
            // 2. Fetch all Categories
            System.out.println("\n[2] Testing Category DAO - Read");
            List<Category> categories = dbService.getCategoryDAO().findAll();
            System.out.println("Categories found in database:");
            for (Category c : categories) {
                System.out.println("  -> ID: " + c.getCategoryId() + ", Name: " + c.getName());
            }
            
            // 3. Update the Category
            System.out.println("\n[3] Testing Category DAO - Update");
            category.setName("Updated Test Category");
            dbService.getCategoryDAO().update(category);
            Category updatedCategory = dbService.getCategoryDAO().findById(category.getCategoryId());
            System.out.println("Category updated. New Name: " + updatedCategory.getName());
            
            // 4. Create a Location
            System.out.println("\n[4] Testing Location DAO - Create");
            Location loc = new Location(10.5, 20.3, "Strada Test, Nr. 1");
            dbService.getLocationDAO().save(loc);
            System.out.println("Location saved. ID is now: " + loc.getLocationId());
            
            // 5. Clean up tests (Delete)
            System.out.println("\n[5] Cleaning up (Delete tests)");
            dbService.getCategoryDAO().delete(category.getCategoryId());
            System.out.println("Test Category deleted.");
            dbService.getLocationDAO().delete(loc.getLocationId());
            System.out.println("Test Location deleted.");
            
            dbService.closeConnection();
            System.out.println("\n=== DAO Test Finished Successfully ===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
