package models;

import interfaces.IAuthenticable;

public class Admin extends User implements IAuthenticable {

    public Admin(String firstName, String secondName, String phone, String email, String password) {
        super(firstName, secondName, phone, email, password);
    }

    @Override
    public boolean login(String email, String password) {
        return this.getEmail().equals(email) && this.getPassword().equals(password);
    }

    //Metode Admin

    public void viewPlatformStats(int totalOrders, int totalCustomers, int totalDrivers, 
                                   int totalRestaurants, double platformRevenue) {

        System.out.println("  Total comenzi:       " + totalOrders);
        System.out.println("  Total clienti:       " + totalCustomers);
        System.out.println("  Total livratori:     " + totalDrivers);
        System.out.println("  Total restaurante:   " + totalRestaurants);
        System.out.println("  Venit platforma:     " + String.format("%.2f", platformRevenue) + " RON");
    }

    public void banUser(User user) {
        System.out.println("  [ADMIN] Utilizatorul " + user.getFirstName() + " " + user.getSecondName() 
            + " (ID: " + user.getUserId() + ") a fost banat.");
    }

    public void approveBusiness(String businessName) {
        System.out.println("  [ADMIN] Business-ul '" + businessName + "' a fost aprobat pe platforma.");
    }

    @Override
    public String toString() {
        return "Admin{"+"userId=" + getUserId() +
                ", name='" + getFirstName() + " " + getSecondName() + '\'' +
                ", email='" + getEmail() + '\'' + '}';
    }
}
