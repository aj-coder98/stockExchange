package com.cams.stock.service;

//import com.cams.stock.model.UserStockHoldings;
//import com.cams.stock.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;

public interface UserStockHoldingsService {
	void addStockHolding(Long userId, Long stockId, int quantity, int pendingBuy, double averageStockPrice);
	void modifyStockHolding(Long userId, Long stockId, int quantity, int newPendingBuy, 
			int newPendingSell, double averageStockPrice);
	void removeStockHolding(Long userId, Long stockId);
}
