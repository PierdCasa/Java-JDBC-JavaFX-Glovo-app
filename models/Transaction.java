package models;

import enums.TransactionType;
import java.time.LocalDateTime;

public class Transaction {
    private static int idCounter = 0;

    private int transactionId;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;
    private int relatedOrderId;

    public Transaction(TransactionType type, double amount, String description, int relatedOrderId) {
        this.transactionId = ++idCounter;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.relatedOrderId = relatedOrderId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getRelatedOrderId() {
        return relatedOrderId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + transactionId +
                ", type=" + type +
                ", amount=" + String.format("%.2f", amount) + " RON" +
                ", desc='" + description + '\'' +
                ", orderId=" + relatedOrderId +
                ", time=" + timestamp.toLocalDate() + " " + timestamp.toLocalTime().withNano(0) +
                '}';
    }
}
