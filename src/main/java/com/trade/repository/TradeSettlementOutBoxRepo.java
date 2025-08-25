package com.trade.repository;

import com.trade.entity.TradeSettlementOutBoxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeSettlementOutBoxRepo extends JpaRepository<TradeSettlementOutBoxEntity ,Long> {
    List<TradeSettlementOutBoxEntity> findByStatusFalse();
}
