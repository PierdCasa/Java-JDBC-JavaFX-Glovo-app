package services;

import enums.VehicleType;
import models.*;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<Customer> customers;
    private List<DeliveryMan> deliveryMen;
    private List<Admin> admins;
    private Customer loggedInCustomer;

    public UserService() {
        this.customers = new ArrayList<>();
        this.deliveryMen = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.loggedInCustomer = null;
    }

    // register

    public Customer register(String firstName, String secondName, String phone, String email, String password,
            String deliveryAddress, Location location) {
        for (Customer c : customers) {
            if (c.getEmail().equals(email)) {
                System.out.println("Eroare: Email-ul '" + email + "' exista deja!");
                return null;
            }
        }
        Customer customer = new Customer(firstName, secondName, phone, email, password, deliveryAddress, location);
        customers.add(customer);
        AuditService.getInstance().logAction("REGISTER_CUSTOMER");
        System.out.println("Contul pentru " + firstName + " " + secondName + " a fost creat!");
        return customer;
    }

    public DeliveryMan registerDeliveryMan(String firstName, String secondName, String phone, String email,
            String password, String licensePlate, VehicleType vehicleType, Location location) {
        DeliveryMan dm = new DeliveryMan(firstName, secondName, phone, email, password, licensePlate, vehicleType,
                location);
        deliveryMen.add(dm);
        AuditService.getInstance().logAction("REGISTER_DELIVERYMAN");
        System.out.println("Livratorul " + firstName + " " + secondName + " a fost inregistrat.");
        return dm;
    }

    public Admin registerAdmin(String firstName, String secondName, String phone, String email, String password) {
        Admin admin = new Admin(firstName, secondName, phone, email, password);
        admins.add(admin);
        AuditService.getInstance().logAction("REGISTER_ADMIN");
        System.out.println("Admin-ul " + firstName + " " + secondName + " a fost inregistrat.");
        return admin;
    }

    // login si logout

    public User login(String email, String password) {
        User user = database.DatabaseService.getInstance().getUserDAO().findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            if (user instanceof Customer) {
                loggedInCustomer = (Customer) user;
            }
            AuditService.getInstance().logAction("LOGIN");
            System.out.println("Bine ai venit, " + user.getFirstName() + " " + user.getSecondName() + "!");
            return user;
        }
        System.out.println("Eroare: Email sau parola incorecta!");
        return null;
    }

    public void logout() {
        if (loggedInCustomer != null) {
            AuditService.getInstance().logAction("LOGOUT");
            System.out.println("La revedere, " + loggedInCustomer.getFirstName() + "!");
            loggedInCustomer = null;
        }
    }

    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public void setLoggedInCustomer(Customer loggedInCustomer) {
        this.loggedInCustomer = loggedInCustomer;
    }

    // lookups

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<DeliveryMan> getDeliveryMen() {
        return new ArrayList<>(deliveryMen);
    }

    public List<Admin> getAdmins() {
        return new ArrayList<>(admins);
    }

    public DeliveryMan findAvailableDeliveryMan(Location restaurantLocation) {
        DeliveryMan bestDm = null;
        double minDistance = Double.MAX_VALUE;
        List<DeliveryMan> allDrivers = database.DatabaseService.getInstance().getUserDAO().findAllDeliveryMen();
        for (DeliveryMan dm : allDrivers) {
            if (dm.isAvailable()) {
                double distance = dm.getCurrentLocation().distanceTo(restaurantLocation);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestDm = dm;
                }
            }
        }
        return bestDm;
    }

    public Customer findCustomerByEmail(String email) {
        for (Customer c : customers) {
            if (c.getEmail().equals(email)) {
                return c;
            }
        }
        return null;
    }
}
