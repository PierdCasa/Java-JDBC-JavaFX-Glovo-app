package models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private static int idCounter = 0;

    private int productId;
    private String name;
    private Category category;
    private String description;
    private double price;
    private List<Ingredient> lockedIngredients;    // mereu incluse (carne, cartofi, paine)
    private List<Ingredient> optionalIngredients;  // clientul alege (sosuri, ceapa crocanta, ardei iute)

    public Product(String name, Category category, String description, double price) {
        this.productId = ++idCounter;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.lockedIngredients = new ArrayList<>();
        this.optionalIngredients = new ArrayList<>();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    //Ingredient managment 

    public void addLockedIngredient(Ingredient ingredient) {
        lockedIngredients.add(new Ingredient(ingredient.getName(), 0.0, true));
        System.out.println("  + Ingredient fix adaugat la '" + name + "': " + ingredient.getName());
    }

    public void addOptionalIngredient(Ingredient ingredient) {
        optionalIngredients.add(ingredient);
        System.out.println("  + Ingredient optional adaugat la '" + name + "': " + ingredient.getName() 
            + " (+" + String.format("%.2f", ingredient.getExtraPrice()) + " RON)");
    }

    public List<Ingredient> getLockedIngredients() {
        return new ArrayList<>(lockedIngredients);
    }

    public List<Ingredient> getOptionalIngredients() {
        return new ArrayList<>(optionalIngredients);
    }

    public boolean hasCustomizations() {
        return !optionalIngredients.isEmpty();
    }

    public void showAvailableCustomizations() {
        if (lockedIngredients.isEmpty() && optionalIngredients.isEmpty()) {
            System.out.println("  Produsul '" + name + "' nu are ingrediente configurabile.");
            return;
        }
        System.out.println("  === Ingrediente pentru '" + name + "' ===");
        if (!lockedIngredients.isEmpty()) {
            System.out.println("  Incluse (fixe):");
            for (Ingredient ing : lockedIngredients) {
                System.out.println("    * " + ing.getName());
            }
        }
        if (!optionalIngredients.isEmpty()) {
            System.out.println("  Optionale:");
            for (int i = 0; i < optionalIngredients.size(); i++) {
                Ingredient ing = optionalIngredients.get(i);
                System.out.println("    " + (i + 1) + ". " + ing.getName() 
                    + " (+" + String.format("%.2f", ing.getExtraPrice()) + " RON)");
            }
        }
    }

    //Implementare adunare a doua produse 
    //Se putea face si cu suprascriere
    public static double add(Product p1, Product p2) {
        return p1.getPrice() + p2.getPrice();
    }

    @Override
    public String toString() {
        return "Product{" + "productId=" + productId +
                ", name='" + name + '\'' +
                ", category=" + (category !=null ? category.getName() : "N/A") +
                ", description='" + description + '\'' +
                ", price=" + price +'}';
    }
}
