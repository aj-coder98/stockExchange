package com.cams.stock.model;

public class PendingTransactions {
	private String stockName;
	private int pending;
	private boolean isBuy;
	
	 // Getter for stockName
    public String getStockName() {
        return stockName;
    }

    // Setter for stockName
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    // Getter for pending
    public int getPending() {
        return pending;
    }

    // Setter for pending
    public void setPending(int pending) {
        this.pending = pending;
    }

    // Getter for isBuy
    public boolean isBuy() {
        return isBuy;
    }

    // Setter for isBuy
    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }
}