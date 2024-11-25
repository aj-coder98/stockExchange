package com.cams.stock.model;

import jakarta.persistence.*;

@Entity
@Table(name="UserStockHoldings")
public class UserStockHoldings {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serialId;
	
	private Long pairId;
	private Long userId;
	private Long stockId;
	private int quantity;
	private double averageStockPrice;
	private int pendingBuy;
	private int pendingSell;
	
    // Getters and Setters
    public Long getPairId() {
    	return pairId;
    }
    
    public void setPairId(Long userId, Long stockId) {
    	this.pairId = (stockId << 7L) + userId;
    	this.stockId = stockId;
    	this.userId = userId;
    }
    
    public Long getStockId() {
    	return stockId;
    }
    
    public Long getUserId() {
    	return userId;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getAverageStockPrice() {
        return averageStockPrice;
    }

    public void setAverageStockPrice(double averageStockPrice) {
        this.averageStockPrice = averageStockPrice;
    }

    public int getPendingBuy() {
        return pendingBuy;
    }

    public void setPendingBuy(int pendingBuy) {
        this.pendingBuy = pendingBuy;
    }

    public int getPendingSell() {
        return pendingSell;
    }

    public void setPendingSell(int pendingSell) {
        this.pendingSell = pendingSell;
    }
}
