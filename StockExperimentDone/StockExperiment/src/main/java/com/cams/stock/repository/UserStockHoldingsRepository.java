package com.cams.stock.repository;

import com.cams.stock.model.UserStockHoldings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockHoldingsRepository extends JpaRepository<UserStockHoldings, Long> {
	Optional<UserStockHoldings> findByPairId(Long pairId);
	List<UserStockHoldings> findByUserId(long userId);
	List<UserStockHoldings> findByStockId(long stockId);
}
