package com.cams.stock.repository;

import com.cams.stock.model.Stock;
import com.cams.stock.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
	Optional<Stock> findByName(String name);
}
