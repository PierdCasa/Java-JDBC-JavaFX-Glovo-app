package models;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private int orderItemId;
    private Product product;
    private int quantity;
    private List<Ingredient> selectedCustomizations; // ingredientele optionale alese de client
    private double price;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.selectedCustomizations = new ArrayList<>();
        this.price = calculateItemPrice();
    }

    public OrderItem(Product product, int quantity, List<Ingredient> customizations) {
        this.product = product;
        this.quantity = quantity;
        this.selectedCustomizations = customizations != null ? new ArrayList<>(customizations) : new ArrayList<>();
        this.price = calculateItemPrice();
    }

    // Pret = (pret produs de baza + suma extra preturi ingrediente optionale) * cantitate
    public double calculateItemPrice() {
        double basePrice = product.getPrice();
        double customizationExtra = 0.0;
        for (Ingredient ing : selectedCustomizations) {
            customizationExtra += ing.getExtraPrice();
        }
        return (basePrice + customizationExtra) * quantity;
    }

    public void addCustomization(Ingredient ingredient) {
        // Verif. daca ingredientul este disponibil ca optional la produs
        boolean found = false;
        for (Ingredient opt : product.getOptionalIngredients()) {
            if (opt.getName().equals(ingredient.getName())) {
                found = true;
                break;
            }
        }
        if (found) {
            selectedCustomizations.add(ingredient);
            this.price = calculateItemPrice();
            System.out.println("  + Personalizare adaugata: " + ingredient.getName() 
                + " (+" + String.format("%.2f", ingredient.getExtraPrice()) + " RON)");
        } else {
            System.out.println("  ! Ingredientul '" + ingredient.getName() 
                + "' nu este disponibil ca optional pentru '" + product.getName() + "'.");
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return product.getName();
    }

    public double getPrice() {
        return price;
    }

    public List<Ingredient> getSelectedCustomizations() {
        return new ArrayList<>(selectedCustomizations);
    }

    public static double add(OrderItem o1, OrderItem o2) {
        return o1.getPrice() + o2.getPrice();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(product.getName())
          .append(" x").append(quantity)
          .append(" = ").append(String.format("%.2f", price)).append(" RON");
        if (!selectedCustomizations.isEmpty()) {
            sb.append("\n      Personalizari: ");
            for (int i = 0; i < selectedCustomizations.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(selectedCustomizations.get(i).getName());
            }
        }
        return sb.toString();
    }
}
