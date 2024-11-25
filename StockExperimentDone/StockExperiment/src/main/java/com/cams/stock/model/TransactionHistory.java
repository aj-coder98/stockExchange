package com.cams.stock.model;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name="TransactionHistory")
public class TransactionHistory {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
	private long transactionId;
	private long userId;
	
	private long stockId;
    
	private String stockName;
    private double price;
    private int totalShares;
    private LocalDateTime time;
    private boolean isBuy;
    
    public long getTransactionId() {
        return transactionId;
    }
 
    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }
 
    // Getter and Setter for userId
    public long getUserId() {
        return userId;
    }
 
    public void setUserId(long userId) {
        this.userId = userId;
    }
 
    // Getter and Setter for stockId
    public long getStockId() {
        return stockId;
    }
 
    public void setStockId(long stockId) {
        this.stockId = stockId;
    }
 
    // Getter and Setter for stockName
    public String getStockName() {
        return stockName;
    }
 
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
 
    // Getter and Setter for curPrice
    public double getPrice() {
        return price;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
 
    // Getter and Setter for totalShares
    public int getTotalShares() {
        return totalShares;
    }
 
    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
 
    // Getter and Setter for time
    public LocalDateTime getTime() {
        return time;
    }
 
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    // Getter method for isBuy
    public boolean isBuy() {
        return isBuy;
    }

    // Setter method for isBuy
    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }
}