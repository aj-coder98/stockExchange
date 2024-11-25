package com.cams.stock.model;

public class WaitListEntry {
    private Long userId;
    private int numberOfShares;
    private boolean isBuy;

    // Constructor for WaitListEntry
    public WaitListEntry(Long userId, int numberOfShares, boolean isBuy) {
        this.userId = userId;
        this.numberOfShares = numberOfShares;
        this.isBuy = isBuy;
    }

    // Getter and Setter for userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Getter and Setter for numberOfShares
    public int getNumberOfShares() {
        return numberOfShares;
    }

    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    // Getter and Setter for isBuy
    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }
}