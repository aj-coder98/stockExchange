package com.cams.stock.serviceImpl;

import com.cams.stock.model.*;
import com.cams.stock.repository.*;
import com.cams.stock.service.UserStockHoldingsService;

import org.springframework.stereotype.Service;

@Service
public class UserStockHoldingsServiceImpl implements UserStockHoldingsService {

	private UserStockHoldingsRepository userStockHoldingsRepository;

    public UserStockHoldingsServiceImpl(UserStockHoldingsRepository userStockHoldingsRepository) {
        this.userStockHoldingsRepository = userStockHoldingsRepository;
    }

    public void addStockHolding(Long userId, Long stockId, int quantity, int pendingBuy, double averageStockPrice) {
    	UserStockHoldings userStockHoldings = new UserStockHoldings();
        userStockHoldings.setPairId(userId, stockId);
        userStockHoldings.setQuantity(quantity);
        userStockHoldings.setPendingBuy(pendingBuy);
        userStockHoldings.setPendingSell(0);
        userStockHoldings.setAverageStockPrice(averageStockPrice);
        
        userStockHoldingsRepository.save(userStockHoldings);
    }
    
    public void modifyStockHolding(Long userId, Long stockId, int newQuantity, int newPendingBuy, int newPendingSell, 
    		double averageStockPrice) {
        Long pairId = (stockId << 7L) + userId;
        
        UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        if (holding != null) {
            holding.setQuantity(newQuantity);
            holding.setPendingBuy(newPendingBuy);
            holding.setPendingSell(newPendingSell);
            holding.setAverageStockPrice(averageStockPrice);
            
            userStockHoldingsRepository.save(holding);
        } else {
            throw new RuntimeException("Stock holding not found");
        }
    }
    
    public void removeStockHolding(Long userId, Long stockId) {
        Long pairId = (stockId << 7L) + userId;
        
        UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        if (holding != null) {
        	userStockHoldingsRepository.delete(holding);
        } else {
            throw new RuntimeException("Stock holding not found");
        }
    }
    
    
}
