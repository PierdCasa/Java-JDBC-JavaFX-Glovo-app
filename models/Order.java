package models;

import enums.OrderStatus;
import interfaces.IOrderObserver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static int idCounter = 0;

    private int orderId;
    private Customer customer;
    private DeliveryMan deliveryMan;
    private Restaurant restaurant;
    private List<OrderItem> orderItems;
    private double orderPrice;        // pretul produselor (fara delivery fee si tip)
    private double deliveryFee;
    private double tipAmount;
    private double platformCommission; // comisionul platformei (procent din pretul comenzii)
    private OrderStatus status;
    private int preparingTime;         //in minute
    private int deliveryTime;          //in minute
    private LocalDateTime orderDate;
    private PromoCode appliedPromoCode;

    //observer pattern 
    private List<IOrderObserver> observers;

    //rata comision platforma
    private static final double COMMISSION_RATE = 0.15;

    public Order(Customer customer, Restaurant restaurant, List<OrderItem> initialItems, double deliveryFee, double tipAmount) {
        this.orderId = ++idCounter;
        this.customer = customer;
        this.deliveryMan = null;
        this.restaurant = restaurant;
        this.orderItems = new ArrayList<>();
        this.deliveryFee = deliveryFee;
        this.tipAmount = tipAmount;
        this.status = OrderStatus.PENDING;
        this.orderDate = LocalDateTime.now();
        this.observers = new ArrayList<>();
        this.appliedPromoCode = null;

        //adaugam produsele initiale la comanda
        if (initialItems != null) {
            for (OrderItem item : initialItems) {
                if (restaurant.hasProduct(item.getProduct())) {
                    this.orderItems.add(item);
                } else {
                    System.out.println("Atentie: Produsul '" + item.getName() + "' nu apartine restaurantului " + restaurant.getName() + " si nu a fost adaugat la comanda.");
                }
            }
        }
        //calculam pretul
        this.orderPrice = calculateProductsPrice();
        this.platformCommission = orderPrice * COMMISSION_RATE;
        this.preparingTime = 0;
        this.deliveryTime = 0;
    }

    //getters

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    public List<Product> getOrderProducts() {
        List<Product> products = new ArrayList<>();
        for (OrderItem item : orderItems) {
            products.add(item.getProduct());
        }
        return products;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getTipAmount() {
        return tipAmount;
    }

    public double getPlatformCommission() {
        return platformCommission;
    }

    public static double getCommissionRate() {
        return COMMISSION_RATE;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setPlatformCommission(double platformCommission) {
        this.platformCommission = platformCommission;
    }

    public PromoCode getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public int getPreparingTime() {
        return preparingTime;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    //setters

    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
        System.out.println("Livratorul " + deliveryMan.getFirstName() + " " + deliveryMan.getSecondName() + " a fost asignat comenzii #" + orderId + ".");
    }

    public void setPreparingTime(int preparingTime) {
        this.preparingTime = preparingTime;
        System.out.println("Timpul de preparare pentru comanda #" + orderId + " este de " + preparingTime + " minute.");
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
        System.out.println("Timpul de livrare pentru comanda #" + orderId + " este de " + deliveryTime + " minute.");
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setTipAmount(double tipAmount) {
        this.tipAmount = tipAmount;
    }

    //promocode

    public void applyPromoCode(PromoCode promo) {
        if (promo == null || !promo.isValid()) {
            System.out.println("  ! Codul promotional nu este valid.");
            return;
        }
        this.appliedPromoCode = promo;
        promo.use();

        if (promo.getDiscountPercent() > 0) {
            double discount = orderPrice * (promo.getDiscountPercent() / 100.0);
            orderPrice -= discount;
            System.out.println("  + Cod promo '" + promo.getCode() + "' aplicat! Discount: " 
                + String.format("%.2f", discount) + " RON (" + String.format("%.0f", promo.getDiscountPercent()) + "%)");
        }
        if (promo.isFreeDelivery()) {
            deliveryFee = 0.0;
            System.out.println("  + Livrare gratuita aplicata!");
        }
        //recalculeaza comisionul 
        this.platformCommission = orderPrice * COMMISSION_RATE;
    }

    //calculeaza pretul produselor

    public double calculateProductsPrice() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getPrice();
        }
        return total;
    }

    //total platit de client
    public double getTotalPrice() {
        return orderPrice + deliveryFee + tipAmount;
    }

    // ce primeste restaurantul (pretul produselor minus comisionul platformei)
    public double getRestaurantPayout() {
        return orderPrice - platformCommission;
    }

    // ce primeste livratorul (taxa livrare + tip)
    public double getDeliveryManPayout() {
        return deliveryFee + tipAmount;
    }

    //add product to order

    public void addProductToOrder(Product product) {
        if (restaurant.hasProduct(product)) {
            orderItems.add(new OrderItem(product, 1));
            this.orderPrice = calculateProductsPrice();
            this.platformCommission = orderPrice * COMMISSION_RATE;
            System.out.println("Produsul '" + product.getName() + "' a fost adaugat la comanda #" + orderId + ". Pret total: " + String.format("%.2f", getTotalPrice()) + " RON.");
        } else {
            System.out.println("Produsul '" + product.getName() + "' nu apartine restaurantului " + restaurant.getName() + " si nu poate fi adaugat la comanda.");
        }
    }

    //state Pattern: Order Status Transitions

    public void updateStatus() {
        OrderStatus oldStatus = status;
        switch (status) {
            case PENDING:
                status = OrderStatus.ACCEPTED;
                System.out.println("Comanda #" + orderId + " a fost acceptata (ACCEPTED).");
                break;
            case ACCEPTED:
                status = OrderStatus.PREPARING;
                System.out.println("Comanda #" + orderId + " se prepara (PREPARING).");
                break;
            case PREPARING:
                status = OrderStatus.PICKED_UP;
                System.out.println("Comanda #" + orderId + " a fost preluata de livrator (PICKED_UP).");
                break;
            case PICKED_UP:
                status = OrderStatus.ARRIVING;
                System.out.println("Comanda #" + orderId + " este in drum (ARRIVING).");
                break;
            case ARRIVING:
                status = OrderStatus.DELIVERED;
                System.out.println("Comanda #" + orderId + " a fost livrata (DELIVERED).");
                break;
            case DELIVERED:
                System.out.println("Comanda #" + orderId + " a fost deja livrata.");
                return; // nu notificam observatorii
            case CANCELLED:
                System.out.println("Comanda #" + orderId + " este anulata si nu poate fi actualizata.");
                return;
        }
        notifyObservers(oldStatus, status);
    }

    public void cancelOrder() {
        if (status == OrderStatus.DELIVERED) {
            System.out.println("Comanda #" + orderId + " nu poate fi anulata - deja livrata.");
            return;
        }
        if (status == OrderStatus.CANCELLED) {
            System.out.println("Comanda #" + orderId + " este deja anulata.");
            return;
        }
        OrderStatus oldStatus = status;
        status = OrderStatus.CANCELLED;
        System.out.println("Comanda #" + orderId + " a fost anulata (CANCELLED).");
        notifyObservers(oldStatus, status);
    }

    //observer pattern 

    public void addObserver(IOrderObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IOrderObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(OrderStatus oldStatus, OrderStatus newStatus) {
        for (IOrderObserver observer : observers) {
            observer.onOrderStatusChanged(this, oldStatus, newStatus);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Comanda #").append(orderId).append(" ===\n");
        sb.append("Client: ").append(customer.getFirstName()).append(" ").append(customer.getSecondName()).append("\n");
        sb.append("Restaurant: ").append(restaurant.getName()).append("\n");
        sb.append("Livrator: ").append(deliveryMan != null ? deliveryMan.getFirstName() + " " + deliveryMan.getSecondName() + " (" + deliveryMan.getVehicleType() + ")" : "Neasignat").append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Data: ").append(orderDate.toLocalDate()).append(" ").append(orderDate.toLocalTime().withNano(0)).append("\n");
        sb.append("Timp preparare: ").append(preparingTime).append(" min\n");
        sb.append("Timp livrare: ").append(deliveryTime).append(" min\n");
        sb.append("Produse comandate:\n");
        for (OrderItem item : orderItems) {
            sb.append("  - ").append(item.toString()).append("\n");
        }
        sb.append("Pret produse: ").append(String.format("%.2f", orderPrice)).append(" RON\n");
        sb.append("Taxa livrare: ").append(String.format("%.2f", deliveryFee)).append(" RON\n");
        sb.append("Tip livrator:  ").append(String.format("%.2f", tipAmount)).append(" RON\n");
        if (appliedPromoCode != null) {
            sb.append("Cod promo:     ").append(appliedPromoCode.getCode()).append("\n");
        }
        sb.append("TOTAL: ").append(String.format("%.2f", getTotalPrice())).append(" RON");
        return sb.toString();
    }
}
