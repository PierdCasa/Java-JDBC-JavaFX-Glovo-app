package models;

import enums.TransactionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Wallet {

    private int walletId;
    private double balance;
    private List<Transaction> transactionHistory;

    public Wallet(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public double getBalance() {
        return balance;
    }

    public boolean hasSufficientFunds(double amount) {
        return balance >= amount;
    }

    public void deposit(double amount, TransactionType type, String description, int orderId) {
        balance += amount;
        Transaction t = new Transaction(type, amount, description, orderId);
        transactionHistory.add(t);
    }

    public boolean withdraw(double amount, TransactionType type, String description, int orderId) {
        if (amount <= 0) {
            System.out.println("  ! Suma invalida pentru retragere.");
            return false;
        }
        if (!hasSufficientFunds(amount)) {
            System.out.println("  ! Fonduri insuficiente. Sold: " + String.format("%.2f", balance) 
                + " RON, Necesar: " + String.format("%.2f", amount) + " RON");
            return false;
        }
        balance -= amount;
        Transaction t = new Transaction(type, -amount, description, orderId);
        transactionHistory.add(t);
        return true;
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public List<Transaction> getTransactionsBetween(LocalDateTime start, LocalDateTime end) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactionHistory) {
            if (!t.getTimestamp().isBefore(start) && !t.getTimestamp().isAfter(end)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    // Calculeaza venitul (doar depozitele/incasarile) intr-o perioada
    public double getRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        double revenue = 0.0;
        for (Transaction t : transactionHistory) {
            if (!t.getTimestamp().isBefore(start) && !t.getTimestamp().isAfter(end)) {
                if (t.getAmount() > 0) {
                    revenue += t.getAmount();
                }
            }
        }
        return revenue;
    }

    public void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("  Nu exista tranzactii.");
            return;
        }
        for (Transaction t : transactionHistory) {
            System.out.println("  " + t);
        }
    }

    @Override
    public String toString() {
        return "Wallet{balance=" + String.format("%.2f", balance) + " RON, tranzactii=" + transactionHistory.size() + "}";
    }
}