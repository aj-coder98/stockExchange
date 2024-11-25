package com.cams.stock.repository;
 
import com.cams.stock.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
	List<TransactionHistory> findByUserId(long userId);
	List<TransactionHistory> findByTimeAfter(LocalDateTime time);
}