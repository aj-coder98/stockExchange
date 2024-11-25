package com.cams.stock.model;

import jakarta.persistence.*;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;

@Entity
@Table(name="Stocks")
public class Stock {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stockId;
    
	private String name;
    private double curPrice;
    private int totalShares;
    private double yesPrice;
    private String stockDesc;
    private long volume;

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for stockId
    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    // Getter and Setter for curPrice
    public double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    // Getter and Setter for totalShares
    public int getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
    
 // Getter and Setter for yesPrice
    public double getYesPrice() {
        return yesPrice;
    }
 
    public void setYesPrice(double yesPrice) {
        this.yesPrice = yesPrice;
    }
 
    // Getter and Setter for stockDesc
    public String getStockDesc() {
        return stockDesc;
    }
 
    public void setStockDesc(String stockDesc) {
        this.stockDesc = stockDesc;
    }
 
    // Getter and Setter for volume
    public long getVolume() {
        return volume;
    }
 
    public void setVolume(long volume) {
        this.volume = volume;
    }
}