package models;

import enums.TransactionType;
import interfaces.IAuthenticable;

public class Customer extends User implements IAuthenticable {
    private String deliveryAddress;
    private Location location;
    private int loyaltyPoints;

    public Customer(String firstName, String secondName, String phone, String email, String password, String deliveryAddress, Location location) {
        super(firstName, secondName, phone, email, password);
        this.deliveryAddress = deliveryAddress;
        this.location = location;
        this.loyaltyPoints = 0;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    //puncte de loialitate

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    // acumuleaza 1 pct. la fiecare ron cheltuiti
    public void addLoyaltyPoints(double amountSpent) {
        int pointsEarned = (int) (amountSpent / 10);
        if (pointsEarned > 0) {
            loyaltyPoints += pointsEarned;
            System.out.println("  + " + pointsEarned + " puncte de loialitate castigate! Total: " + loyaltyPoints);
        }
    }

    //converteste 1 pct in credit 
    public boolean redeemPoints(int points) {
        if (points <= 0 || points > loyaltyPoints) {
            System.out.println("  ! Puncte insuficiente. Disponibil: " + loyaltyPoints);
            return false;
        }
        if (points % 10 != 0) {
            System.out.println("  ! Punctele trebuie rascumparate in multipli de 10.");
            return false;
        }
        double credit = (points / 10) * 5.0;
        loyaltyPoints -= points;
        getWallet().deposit(credit, TransactionType.LOYALTY_REDEEM, "Rascumparare " + points + " puncte loialitate", 0);
        System.out.println("  + " + points + " puncte rascumparate pentru " + String.format("%.2f", credit) + " RON. Puncte ramase: " + loyaltyPoints);
        return true;
    }

    @Override
    public boolean login(String email, String password) {
        return this.getEmail().equals(email) && this.getPassword().equals(password);
    }

    @Override
    public String toString() {
        return "Customer{"+"userId=" + getUserId() +
                ", name='" + getFirstName() + " " + getSecondName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", wallet=" + getWallet() + '}';
    }
}
