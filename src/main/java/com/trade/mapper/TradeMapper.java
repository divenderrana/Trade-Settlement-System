package com.trade.mapper;


import com.trade.dto.Trade;
import com.trade.entity.TradeEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;


@Component
public class TradeMapper {

    public static TradeEntity toEntity(Trade dto) {
        BigDecimal quantity = new BigDecimal(dto.getQuantity());

        BigDecimal settlementAmount=dto.getPrice().multiply(quantity);
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setTradeId(dto.getTradeId());
        tradeEntity.setSymbol(dto.getSymbol());
        tradeEntity.setQuantity(dto.getQuantity());
        tradeEntity.setPrice(dto.getPrice());
        tradeEntity.setSide(dto.getSide());
        tradeEntity.setTraderId(dto.getTraderId());
        tradeEntity.setTradeDate(dto.getTradeDate() != null ? Instant.from(dto.getTradeDate()) : Instant.now());
        tradeEntity.setSettlementDate( Instant.now());
        tradeEntity.setStatus(String.valueOf(dto.getStatus()));
        tradeEntity.setSettlementAmount(settlementAmount);
        return tradeEntity;
    }
}
