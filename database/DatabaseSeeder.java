package database;

import enums.UserRole;
import models.*;

public class DatabaseSeeder {

    public static void seedIfNeeded() {
        DatabaseService db = DatabaseService.getInstance();

        // Suppress misleading console outputs from models during load
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
            }
        }));

        boolean hasRestaurants = !db.getRestaurantDAO().findAll().isEmpty();

        System.setOut(originalOut);

        // If restaurants already exist, don't seed
        if (hasRestaurants) {
            System.out.println("[Seeder] Baza de date contine deja date. Se omite inserarea.");
            
            // Check if users exist and insert them if they were deleted
            if (db.getUserDAO().findByEmail("maria@email.com") == null) {
                Location userLoc = new Location(15.0, 20.0, "Str. Floreasca 10");
                db.getLocationDAO().save(userLoc);
                Customer c1 = new Customer("Maria", "Dumitrescu", "0774567890", "maria@email.com", "maria123",
                        "Str. Floreasca 10", userLoc);
                c1.getWallet().setWalletId(0); // It will be created
                c1.getWallet().deposit(0.0, enums.TransactionType.DEPOSIT, "Top-up initial", 0);
                db.getUserDAO().save(c1);
            }
            if (db.getUserDAO().findByEmail("andrei@email.com") == null) {
                Location driverLoc = new Location(20.0, 25.0, "Bd. Unirii 45");
                db.getLocationDAO().save(driverLoc);
                DeliveryMan dm1 = new DeliveryMan("Andrei", "Popescu", "0712345678", "andrei@email.com", "andrei123", "B-123-ABC", enums.VehicleType.CAR, driverLoc);
                dm1.getWallet().setWalletId(0);
                db.getUserDAO().save(dm1);
            }
            
            return;
        }

        System.out.println("[Seeder] Baza de date este goala. Se insereaza date mock...");

        // 1. Categories
        Category pizza = new Category("Pizza");
        db.getCategoryDAO().save(pizza);
        Category burger = new Category("Burgeri");
        db.getCategoryDAO().save(burger);
        Category desert = new Category("Deserturi");
        db.getCategoryDAO().save(desert);
        Category bauturi = new Category("Bauturi");
        db.getCategoryDAO().save(bauturi);

        // 2. Ingredients
        Ingredient extraCheese = new Ingredient("Extra Branza", 4.0, false);
        Ingredient jalapeno = new Ingredient("Jalapeno", 2.5, true);
        Ingredient bacon = new Ingredient("Bacon", 5.0, false);
        db.getIngredientDAO().save(extraCheese);
        db.getIngredientDAO().save(jalapeno);
        db.getIngredientDAO().save(bacon);

        // 3. Products
        Product p1 = new Product("Pizza Margherita", pizza, "Sos rosii, mozzarella", 35.0);
        p1.addOptionalIngredient(extraCheese);
        db.getProductDAO().save(p1);

        Product p2 = new Product("Pizza Pepperoni", pizza, "Sos rosii, mozzarella, salam", 40.0);
        db.getProductDAO().save(p2);

        Product p3 = new Product("Classic Burger", burger, "Vita, salata, rosii, sos", 45.0);
        p3.addOptionalIngredient(jalapeno);
        p3.addOptionalIngredient(bacon);
        db.getProductDAO().save(p3);

        Product p4 = new Product("Tiramisu", desert, "Mascarpone, cafea", 25.0);
        db.getProductDAO().save(p4);

        Product p5 = new Product("Coca-Cola", bauturi, "330ml", 8.0);
        db.getProductDAO().save(p5);

        // 4. Locations & Wallets
        Location loc1 = new Location(10.0, 20.0, "Str. Victoriei 12");
        Location loc2 = new Location(20.0, 25.0, "Bd. Unirii 45");
        db.getLocationDAO().save(loc1);
        db.getLocationDAO().save(loc2);

        // 5. Restaurants
        Restaurant r1 = new Restaurant("La Italiano", "Str. Victoriei 12", 4.8, loc1);
        r1.addProduct(p1);
        r1.addProduct(p2);
        r1.addProduct(p4);
        r1.addProduct(p5);
        db.getRestaurantDAO().save(r1);

        Restaurant r2 = new Restaurant("Burger House", "Bd. Unirii 45", 4.5, loc2);
        r2.addProduct(p3);
        r2.addProduct(p5);
        db.getRestaurantDAO().save(r2);

        // 6. Test User (Customer)
        Location userLoc = new Location(15.0, 20.0, "Str. Floreasca 10");
        db.getLocationDAO().save(userLoc);

        Customer c1 = new Customer("Maria", "Dumitrescu", "0774567890", "maria@email.com", "maria123",
                "Str. Floreasca 10", userLoc);
        c1.getWallet().setWalletId(0); // It will be created
        c1.getWallet().deposit(0.0, enums.TransactionType.DEPOSIT, "Top-up initial", 0);
        db.getUserDAO().save(c1);

        DeliveryMan dm1 = new DeliveryMan("Andrei", "Popescu", "0712345678", "andrei@email.com", "andrei123", "B-123-ABC", enums.VehicleType.CAR, loc2);
        dm1.getWallet().setWalletId(0);
        db.getUserDAO().save(dm1);

        System.out.println("[Seeder] Initializare completa. Login de test: maria@email.com / maria123 | andrei@email.com / andrei123");
    }
}
