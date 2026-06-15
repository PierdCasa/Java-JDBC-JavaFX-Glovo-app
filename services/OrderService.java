package services;

import enums.OrderStatus;
import interfaces.IDeliveryFeeStrategy;
import interfaces.IOrderObserver;
import models.*;

import java.util.*;

public class OrderService implements IOrderObserver {
    private Map<Integer, Order> orders;
    private IDeliveryFeeStrategy deliveryFeeStrategy;
    private PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.orders = new LinkedHashMap<>();
        this.deliveryFeeStrategy = new StandardDeliveryStrategy(); // Default strategy
        this.paymentService = paymentService;
    }

    //strategy pattern: schimba strategia de livrare 

    public void setDeliveryFeeStrategy(IDeliveryFeeStrategy strategy) {
        this.deliveryFeeStrategy = strategy;
        System.out.println("  Strategie livrare schimbata la: " + strategy.getStrategyName());
    }

    public IDeliveryFeeStrategy getDeliveryFeeStrategy() {
        return deliveryFeeStrategy;
    }

    //calculeaza taxa de livrare bazat pe distanta si tipul vehiculului
    public double calculateDeliveryFee(Location from, Location to, DeliveryMan driver) {
        double distance = from.distanceTo(to);
        double fee = deliveryFeeStrategy.calculateFee(distance, driver.getVehicleType());
        return Math.round(fee * 100.0) / 100.0; // rotunjire la 2 zecimale
    }

    //plasare comanda

    public Order placeOrder(Customer customer, Restaurant restaurant, List<OrderItem> items, 
                            DeliveryMan deliveryMan, double tipAmount, PromoCode promoCode) {
        if (customer == null) {
            System.out.println("Trebuie sa fii logat!");
            return null;
        }
        if (restaurant == null || items == null || items.isEmpty()) {
            System.out.println("Date invalide!");
            return null;
        }
        if (deliveryMan == null) {
            System.out.println("Nu sunt livratori disponibili!");
            return null;
        }

        // Calculeaza delivery fee
        double deliveryFee = 0.0;
        if (customer.getLocation() != null && restaurant.getLocationCoords() != null) {
            deliveryFee = calculateDeliveryFee(restaurant.getLocationCoords(), customer.getLocation(), deliveryMan);
        }

        // Creaza comanda
        Order newOrder = new Order(customer, restaurant, items, deliveryFee, tipAmount);
        
        // Aplica promo code daca exista
        if (promoCode != null) {
            newOrder.applyPromoCode(promoCode);
        }

        // Asigneaza livratorul
        newOrder.setDeliveryMan(deliveryMan);

        // Adauga observer (acest OrderService) pentru a urmari schimbarile
        newOrder.addObserver(this);

        // Seteaza timpi random
        Random random = new Random();
        newOrder.setPreparingTime(10 + random.nextInt(31));
        newOrder.setDeliveryTime(10 + random.nextInt(21));

        // Proceseaza plata
        boolean paymentSuccess = paymentService.processPayment(newOrder);
        if (!paymentSuccess) {
            System.out.println("  ! Comanda anulata din cauza platii esuate.");
            return null;
        }

        orders.put(newOrder.getOrderId(), newOrder);
        
        // Save to Database
        database.DatabaseService.getInstance().getOrderDAO().save(newOrder);

        AuditService.getInstance().logAction("PLACE_ORDER");

        System.out.println("\nComanda #" + newOrder.getOrderId() + " plasata cu succes!");
        System.out.println("  Pret produse: " + String.format("%.2f", newOrder.getOrderPrice()) + " RON");
        System.out.println("  Taxa livrare: " + String.format("%.2f", newOrder.getDeliveryFee()) + " RON (" + deliveryFeeStrategy.getStrategyName() + ")");
        System.out.println("  Tip livrator: " + String.format("%.2f", newOrder.getTipAmount()) + " RON");
        System.out.println("  TOTAL: " + String.format("%.2f", newOrder.getTotalPrice()) + " RON");

        return newOrder;
    }

    // Observer Pattern: cand se schimba statusul

    @Override
    public void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        // Update database
        database.DatabaseService.getInstance().getOrderDAO().update(order);
        
        AuditService.getInstance().logAction("CHANGE_ORDER_STATUS");
        System.out.println("  [Observer] Comanda #" + order.getOrderId() + ": " + oldStatus + " -> " + newStatus);

        // Daca comanda a fost livrata, facem settlement-ul
        if (newStatus == OrderStatus.DELIVERED) {
            System.out.println("  [Observer] Initiez settlement financiar pentru comanda #" + order.getOrderId());
            paymentService.settleOrder(order);
        }

        // Daca comanda a fost anulata, procesam refund
        if (newStatus == OrderStatus.CANCELLED) {
            System.out.println("  [Observer] Initiez refund pentru comanda #" + order.getOrderId());
            paymentService.processRefund(order);
        }
    }
    
    //  Manual Status Update pt. UI 
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        if (order.getStatus() == newStatus) return;
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        onOrderStatusChanged(order, oldStatus, newStatus);
    }

    // Status & Queries 
    
    public void checkStatus(Order order) {
        if (order == null) {
            System.out.println("Comanda nu exista!");
            return;
        }
        System.out.println("\n" + order.toString());
    }

    public Order getOrderById(int orderId) {
        return orders.get(orderId);
    }

    public void showAllOrders() {
        System.out.println("\n=== Toate comenzile ===");
        if (orders.isEmpty()) {
            System.out.println("Nu exista comenzi.");
            return;
        }
        for (Order o : orders.values()) {
            System.out.println(o);
            System.out.println();
        }
    }

    public void showCustomerOrders(Customer customer) {
        System.out.println("\n=== Comenzile clientului " + customer.getFirstName() + " ===");
        boolean found = false;
        for (Order o : orders.values()) {
            if (o.getCustomer().getUserId() == customer.getUserId()) {
                System.out.println(o);
                found = true;
            }
        }
        if (!found)
            System.out.println("Nu are comenzi.");
    }

    public int getTotalOrders() {
        return orders.size();
    }

    public Map<Integer, Order> getOrders() {
        return new LinkedHashMap<>(orders);
    }
}
