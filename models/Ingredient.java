package models;

public class Ingredient {
    private int ingredientId;
    private String name;
    private double extraPrice;  // 0.0 pentru ingrediente locked
    private boolean locked;     // true = mereu inclus, false = optional

    public Ingredient(String name, double extraPrice, boolean locked) {
        this.name = name;
        this.extraPrice = locked ? 0.0 : extraPrice; // locked = fara cost extra
        this.locked = locked;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public double getExtraPrice() {
        return extraPrice;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public String toString() {
        if (locked) {
            return name + " [inclus]";
        }
        return name + " [optional, +" + String.format("%.2f", extraPrice) + " RON]";
    }
}
