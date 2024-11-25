package com.cams.stock.serviceImpl;

import java.util.Queue;
import java.time.*;
import org.springframework.stereotype.Service;

import com.cams.stock.model.*;
import com.cams.stock.repository.*;
import com.cams.stock.service.TransactionService;
import com.cams.stock.service.UserStockHoldingsService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private UserRepository userRepository;
    private UserStockHoldingsRepository userStockHoldingsRepository;
    private StockRepository stockRepository;
    private UserStockHoldingsService userStockHoldingsService;
    private TransactionHistoryRepository transactionHistoryRepository;
    
	public TransactionServiceImpl(UserRepository userRepository, 
			UserStockHoldingsRepository userStockHoldingsRepository,
			UserStockHoldingsService userStockHoldingsService,
			TransactionHistoryRepository transactionHistoryRepository,
			StockRepository stockRepository) {
        this.userRepository = userRepository;
        this.userStockHoldingsRepository = userStockHoldingsRepository;
        this.userStockHoldingsService = userStockHoldingsService;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.stockRepository = stockRepository;
    }
    
	// Also update transaction table
    public void updateBuyStock(Long userId, Long stockId, int toBuy) {
        Long pairId = userId + (stockId << 7L);
        
        UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        Stock stock = stockRepository.findById(stockId).orElse(null);
        
        if(holding != null) {
        	System.out.println(userId + " has bought stock " + stockId + " : " + toBuy + " shares");
        	
        	double x = holding.getAverageStockPrice();
        	int q = holding.getQuantity();
        	
        	int b = toBuy;
        	double c = stock.getCurPrice();
        	
//        	(x * q + c * b) / (q + b)
        	
        	userStockHoldingsService.modifyStockHolding(userId,	stockId, holding.getQuantity() + toBuy,
        			holding.getPendingBuy() - toBuy, holding.getPendingSell(), (x * q + c * b) / (q + b));
        } else {
        	System.out.println(userId + " has bought stock " + stockId + " : " + toBuy + " shares");
        	userStockHoldingsService.addStockHolding(userId, stockId, toBuy, 0, stock.getCurPrice());
        }
        
        
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setStockId(stockId);
        transactionHistory.setUserId(userId);
        transactionHistory.setStockName(stock.getName());
        transactionHistory.setPrice(toBuy * stock.getCurPrice());
        transactionHistory.setTotalShares(toBuy);
        transactionHistory.setTime(LocalDateTime.now());
        transactionHistory.setBuy(true);
        
        transactionHistoryRepository.save(transactionHistory);
        stock.setVolume(stock.getVolume() + toBuy);
    }
    
    public void updateSellStock(Long userId, Long stockId, int toSell) {
        Long pairId = userId + (stockId << 7L);
        
        UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        Stock stock = stockRepository.findById(stockId).orElse(null);
        
        double x = holding.getAverageStockPrice();
    	int q = holding.getQuantity();
    	
    	int s = toSell;
    	double c = stock.getCurPrice();
    	
        if(holding.getQuantity() == toSell) {
        	System.out.println(userId + " has sold stock " + stockId + " : " + toSell + " shares");
        	userStockHoldingsService.removeStockHolding(userId, stockId);
        } else {
        	System.out.println(userId + " has sold stock " + stockId + " : " + toSell + " shares");
        	userStockHoldingsService.modifyStockHolding(userId, stockId, holding.getQuantity() - toSell,
        			holding.getPendingBuy(), holding.getPendingSell() - toSell, (x * q - c * s) / (q - s));
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user.getUsername().equals("admin")) {
        	stock.setTotalShares(stock.getTotalShares() - toSell);
        	stockRepository.save(stock);
        }
        
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setStockId(stockId);
        transactionHistory.setUserId(userId);
        transactionHistory.setStockName(stock.getName());
        transactionHistory.setPrice(toSell * stock.getCurPrice());
        transactionHistory.setTotalShares(toSell);
        transactionHistory.setTime(LocalDateTime.now());
        transactionHistory.setBuy(false);
        
        transactionHistoryRepository.save(transactionHistory);
    }
	
	@Override
	public void addToWaitList(WaitListEntry entry, Queue<WaitListEntry> waitList, Long stockId) {
		// Update pendingBuy / pendingSell
		Long pairId = entry.getUserId() + (stockId << 7L);
        
        UserStockHoldings holding = userStockHoldingsRepository.findByPairId(pairId).orElse(null);
        if (holding != null) {
        	if (entry.isBuy()) {
        		userStockHoldingsService.modifyStockHolding(entry.getUserId(), stockId, 
        			holding.getQuantity(), holding.getPendingSell() + entry.getNumberOfShares(), 0, holding.getAverageStockPrice());
        	} else {
        		userStockHoldingsService.modifyStockHolding(entry.getUserId(), stockId, 
            			holding.getQuantity(), 0, holding.getPendingSell() + entry.getNumberOfShares(), holding.getAverageStockPrice());
        	}
        } else {
        	userStockHoldingsService.addStockHolding(entry.getUserId(), stockId, 0, 
        			entry.getNumberOfShares(), 0);
        }
        
		
        if (waitList.isEmpty())
            waitList.add(entry);
        else if (waitList.peek().isBuy() == entry.isBuy())
            waitList.add(entry);
        else {
            int originalNumberOfShares = entry.getNumberOfShares();
            while (!waitList.isEmpty() && entry.getNumberOfShares() > 0) {
                if (waitList.peek().getNumberOfShares() <= entry.getNumberOfShares()) {
                    WaitListEntry removedWaitList = waitList.remove(); 
                    // Update the buy / sale
//                    User currentTransaction1 = userClass.get(removedWaitList.getUserId());
                    User currentTransaction1 = userRepository.findById(removedWaitList.getUserId()).orElse(null);
                    if(removedWaitList.isBuy()) {
//                        currentTransaction1.buyStock(stockId, removedWaitList.getNumberOfShares());
                    	updateBuyStock(currentTransaction1.getUserId(), stockId, removedWaitList.getNumberOfShares());
                    	
                    } else {
//                        currentTransaction1.sellStock(stockId, removedWaitList.getNumberOfShares());
                    	updateSellStock(currentTransaction1.getUserId(), stockId, removedWaitList.getNumberOfShares());
                    	
                    }

                    entry.setNumberOfShares(entry.getNumberOfShares() - removedWaitList.getNumberOfShares());

                    if (entry.getNumberOfShares() == 0) {
//                        User currentTransaction2 = userClass.get(entry.getUserId());
                        User currentTransaction2 = userRepository.findById(entry.getUserId()).orElse(null);
                        if(entry.isBuy()) {
//                            currentTransaction2.buyStock(stockId, originalNumberOfShares - entry.getNumberOfShares());
                        	updateBuyStock(currentTransaction2.getUserId(), stockId, 
                        			originalNumberOfShares - entry.getNumberOfShares());
                        } else {
//                            currentTransaction2.sellStock(stockId, originalNumberOfShares - entry.getNumberOfShares());
                        	updateSellStock(currentTransaction2.getUserId(), stockId, 
                        			originalNumberOfShares - entry.getNumberOfShares());
                        }
                    }
                } else {
                    // Buy / Sell original request completely
//                    User currentTransaction1 = userClass.get(entry.getUserId());
                    User currentTransaction1 = userRepository.findById(entry.getUserId()).orElse(null);
                    
                    if(entry.isBuy()) {
//                        currentTransaction1.buyStock(stockId, originalNumberOfShares);
                        updateBuyStock(currentTransaction1.getUserId(), stockId, originalNumberOfShares);
                    	
                    } else {
//                        currentTransaction1.sellStock(stockId, originalNumberOfShares);
                        updateSellStock(currentTransaction1.getUserId(), stockId, originalNumberOfShares);
                    }

                    WaitListEntry topTransaction = waitList.peek();
                    topTransaction.setNumberOfShares(topTransaction.getNumberOfShares() - entry.getNumberOfShares());

//                    User currentTransaction2 = userClass.get(topTransaction.getUserId());
                    User currentTransaction2 = userRepository.findById(topTransaction.getUserId()).orElse(null);
                    
                    if(topTransaction.isBuy()) {
//                        currentTransaction2.buyStock(stockId, entry.getNumberOfShares());
                        updateBuyStock(currentTransaction2.getUserId(), stockId, entry.getNumberOfShares());
                    } else {
//                        currentTransaction2.sellStock(stockId, entry.getNumberOfShares());
                    	updateSellStock(currentTransaction2.getUserId(), stockId, entry.getNumberOfShares());
                    }

                    entry.setNumberOfShares(0);    
                }
            }

            if (entry.getNumberOfShares() != 0) {
                waitList.add(entry);

//                User currentTransaction = userClass.get(entry.getUserId());
                User currentTransaction = userRepository.findById(entry.getUserId()).orElse(null);
                
                if(entry.isBuy()) {
//                    currentTransaction.buyStock(stockId, originalNumberOfShares - entry.getNumberOfShares());
                    updateBuyStock(currentTransaction.getUserId(), stockId, 
                			originalNumberOfShares - entry.getNumberOfShares());
                } else {
//                    currentTransaction.sellStock(stockId, originalNumberOfShares - entry.getNumberOfShares());
                    updateSellStock(currentTransaction.getUserId(), stockId, 
                			originalNumberOfShares - entry.getNumberOfShares());
                }
            }
        }
	}
	
    
}
