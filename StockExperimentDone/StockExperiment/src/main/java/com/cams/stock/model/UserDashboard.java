package com.cams.stock.model;

public class UserDashboard {
	private String stockName;
	private int quantity;
	private double avgInvValue;
	private double invValue;
	private double curValue;
	
	// Getter for stockName
    public String getStockName() {
        return stockName;
    }

    // Setter for stockName
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    // Getter for quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter for avgInvValue
    public double getAvgInvValue() {
        return avgInvValue;
    }

    // Setter for avgInvValue
    public void setAvgInvValue(double avgInvValue) {
        this.avgInvValue = avgInvValue;
    }

    // Getter for invValue
    public double getInvValue() {
        return invValue;
    }

    // Setter for invValue
    public void setInvValue(double invValue) {
        this.invValue = invValue;
    }

    // Getter for curValue
    public double getCurValue() {
        return curValue;
    }

    // Setter for curValue
    public void setCurValue(double curValue) {
        this.curValue = curValue;
    }
}