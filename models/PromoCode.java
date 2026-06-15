package models;

public class PromoCode {
    private int promoCodeId;
    private String code;            // e.g., "MINUS20"
    private double discountPercent; // e.g., 20.0 pentru 20%
    private boolean freeDelivery;   // true = livrare gratuita
    private boolean active;
    private int maxUses;
    private int currentUses;

    public PromoCode(String code, double discountPercent, boolean freeDelivery, int maxUses) {
        this.code = code.toUpperCase();
        this.discountPercent = discountPercent;
        this.freeDelivery = freeDelivery;
        this.active = true;
        this.maxUses = maxUses;
        this.currentUses = 0;
    }

    public String getCode() {
        return code;
    }

    public int getPromoCodeId() {
        return promoCodeId;
    }

    public void setPromoCodeId(int promoCodeId) {
        this.promoCodeId = promoCodeId;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public boolean isFreeDelivery() {
        return freeDelivery;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isValid() {
        return active && currentUses < maxUses;
    }

    public void use() {
        if (isValid()) {
            currentUses++;
            if (currentUses >= maxUses) {
                active = false;
            }
        }
    }

    public int getRemainingUses() {
        return maxUses - currentUses;
    }

    public void deactivate() {
        this.active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getCurrentUses() {
        return currentUses;
    }

    public void setCurrentUses(int currentUses) {
        this.currentUses = currentUses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PromoCode{code='").append(code).append("'");
        if (discountPercent > 0) {
            sb.append(", discount=").append(String.format("%.0f", discountPercent)).append("%");
        }
        if (freeDelivery) {
            sb.append(", livrare gratuita");
        }
        sb.append(", folosiri ramase=").append(getRemainingUses());
        sb.append(", activ=").append(active).append("}");
        return sb.toString();
    }
}
