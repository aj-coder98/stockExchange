package com.cams.stock.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cams.stock.model.Stock;
import com.cams.stock.repository.StockRepository;
import com.cams.stock.service.StockCollectionService;

@Service
public class StockCollectionServiceImpl implements StockCollectionService {

    private StockRepository stockRepository;
    
	public StockCollectionServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
	
	@Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
	
}
