package services;

import enums.TransactionType;
import models.*;

public class PaymentService {
    private Wallet platformWallet; // portofelul escrow al platformei

    public PaymentService() {
        this.platformWallet = new Wallet(0.0);
    }

    public Wallet getPlatformWallet() {
        return platformWallet;
    }

    // Clientul plateste totalul comenzii (produse + delivery fee + tip)
    public boolean processPayment(Order order) {
        Customer customer = order.getCustomer();
        double totalAmount = order.getTotalPrice();

        if (!customer.getWallet().hasSufficientFunds(totalAmount)) {
            System.out.println("  ! Plata esuata: fonduri insuficiente pentru comanda #" + order.getOrderId());
            System.out.println("    Sold: " + String.format("%.2f", customer.getWallet().getBalance()) 
                + " RON | Necesar: " + String.format("%.2f", totalAmount) + " RON");
            return false;
        }

        // 1. Retrage din portofelul clientului
        customer.getWallet().withdraw(totalAmount, TransactionType.PAYMENT, 
            "Plata comanda #" + order.getOrderId(), order.getOrderId());
        database.DatabaseService.getInstance().getWalletDAO().update(customer.getWallet());
        database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.PAYMENT, -totalAmount, "Plata comanda #" + order.getOrderId(), order.getOrderId()), customer.getWallet().getWalletId());

        // 2. Depune in portofelul platformei (escrow)
        platformWallet.deposit(totalAmount, TransactionType.PAYMENT, 
            "Incasare comanda #" + order.getOrderId(), order.getOrderId());

        System.out.println("  $ Plata procesata: " + String.format("%.2f", totalAmount) 
            + " RON din contul lui " + customer.getFirstName());

        // 3. Acumuleaza puncte de loialitate
        customer.addLoyaltyPoints(order.getOrderPrice());

        AuditService.getInstance().logAction("PROCESS_PAYMENT");

        return true;
    }

    // Dupa livrare: distribuie banii din escrow catre restaurant si livrator
    public void settleOrder(Order order) {
        double restaurantPayout = order.getRestaurantPayout();
        double deliveryManPayout = order.getDeliveryManPayout();
        double commission = order.getPlatformCommission();

        Restaurant restaurant = order.getRestaurant();
        DeliveryMan deliveryMan = order.getDeliveryMan();

        // 1. Plateste restaurantul
        platformWallet.withdraw(restaurantPayout, TransactionType.RESTAURANT_PAYOUT, 
            "Plata restaurant '" + restaurant.getName() + "' comanda #" + order.getOrderId(), order.getOrderId());
        restaurant.getWallet().deposit(restaurantPayout, TransactionType.RESTAURANT_PAYOUT, 
            "Incasare comanda #" + order.getOrderId(), order.getOrderId());
        database.DatabaseService.getInstance().getWalletDAO().update(restaurant.getWallet());
        database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.RESTAURANT_PAYOUT, restaurantPayout, "Incasare comanda #" + order.getOrderId(), order.getOrderId()), restaurant.getWallet().getWalletId());

        // 2. Plateste livratorul (delivery fee)
        if (deliveryMan != null && order.getDeliveryFee() > 0) {
            platformWallet.withdraw(order.getDeliveryFee(), TransactionType.DELIVERY_FEE, 
                "Plata livrare " + deliveryMan.getFirstName() + " comanda #" + order.getOrderId(), order.getOrderId());
            deliveryMan.getWallet().deposit(order.getDeliveryFee(), TransactionType.DELIVERY_FEE, 
                "Incasare taxa livrare comanda #" + order.getOrderId(), order.getOrderId());
            database.DatabaseService.getInstance().getWalletDAO().update(deliveryMan.getWallet());
            database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.DELIVERY_FEE, order.getDeliveryFee(), "Incasare taxa livrare comanda #" + order.getOrderId(), order.getOrderId()), deliveryMan.getWallet().getWalletId());
        }

        // 3. Plateste tip-ul livratorului
        if (deliveryMan != null && order.getTipAmount() > 0) {
            platformWallet.withdraw(order.getTipAmount(), TransactionType.TIP, 
                "Tip livrator " + deliveryMan.getFirstName() + " comanda #" + order.getOrderId(), order.getOrderId());
            deliveryMan.getWallet().deposit(order.getTipAmount(), TransactionType.TIP, 
                "Tip primit comanda #" + order.getOrderId(), order.getOrderId());
            database.DatabaseService.getInstance().getWalletDAO().update(deliveryMan.getWallet());
            database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.TIP, order.getTipAmount(), "Tip primit comanda #" + order.getOrderId(), order.getOrderId()), deliveryMan.getWallet().getWalletId());
        }

        // 4. Comisionul ramane in portofelul platformei (deja acolo)
        // Inregistram tranzactia de comision
        platformWallet.deposit(0, TransactionType.COMMISSION, 
            "Comision " + String.format("%.0f", Order.getCommissionRate() * 100) + "% comanda #" + order.getOrderId() 
            + " = " + String.format("%.2f", commission) + " RON", order.getOrderId());

        System.out.println("  $ Settlement comanda #" + order.getOrderId() + ":");
        System.out.println("    Restaurant '" + restaurant.getName() + "': +" + String.format("%.2f", restaurantPayout) + " RON");
        if (deliveryMan != null) {
            System.out.println("    Livrator " + deliveryMan.getFirstName() + ": +" 
                + String.format("%.2f", deliveryManPayout) + " RON (livrare: " 
                + String.format("%.2f", order.getDeliveryFee()) + " + tip: " 
                + String.format("%.2f", order.getTipAmount()) + ")");
        }
        System.out.println("    Comision platforma: " + String.format("%.2f", commission) + " RON");
        AuditService.getInstance().logAction("SETTLE_ORDER");
    }

    // Refund in caz de anulare
    public void processRefund(Order order) {
        double totalAmount = order.getTotalPrice();
        Customer customer = order.getCustomer();

        platformWallet.withdraw(totalAmount, TransactionType.REFUND, 
            "Refund comanda #" + order.getOrderId(), order.getOrderId());
        customer.getWallet().deposit(totalAmount, TransactionType.REFUND, 
            "Refund comanda #" + order.getOrderId(), order.getOrderId());
            
        database.DatabaseService.getInstance().getWalletDAO().update(customer.getWallet());
        database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.REFUND, totalAmount, "Refund comanda #" + order.getOrderId(), order.getOrderId()), customer.getWallet().getWalletId());

        System.out.println("  $ Refund procesat: " + String.format("%.2f", totalAmount) 
            + " RON catre " + customer.getFirstName());
        AuditService.getInstance().logAction("PROCESS_REFUND");
    }

    // Depune bani in portofelul unui user (simulare top-up card)
    public void topUpWallet(User user, double amount) {
        user.getWallet().deposit(amount, TransactionType.DEPOSIT, 
            "Top-up portofel", 0);
        database.DatabaseService.getInstance().getWalletDAO().update(user.getWallet());
        database.DatabaseService.getInstance().getTransactionDAO().save(new Transaction(TransactionType.DEPOSIT, amount, "Top-up portofel", 0), user.getWallet().getWalletId());
        System.out.println("  $ Top-up: " + String.format("%.2f", amount) + " RON depus in contul lui " 
            + user.getFirstName() + ". Sold nou: " + String.format("%.2f", user.getWallet().getBalance()) + " RON");
        AuditService.getInstance().logAction("TOP_UP_WALLET");
    }

    public double getPlatformRevenue() {
        return platformWallet.getBalance();
    }
}
