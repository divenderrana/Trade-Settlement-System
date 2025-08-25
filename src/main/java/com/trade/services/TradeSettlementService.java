package com.trade.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.consumer.TradeConsumer;
import com.trade.dto.Trade;
import com.trade.producer.TradeProducer;
import com.trade.repository.TradeRepo;
import com.trade.request.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TradeSettlementService {

    private TradeProducer tradeProducer;
    @Autowired
    TradeConsumer tradeConsumer;

    @Autowired
    TradeRepo tradeRepo;

    private final ObjectMapper objectMapper;
    public void TradeService(TradeProducer tradeProducer) {
        this.tradeProducer = tradeProducer;
    }

    public TradeSettlementService(TradeProducer tradeProducer, ObjectMapper objectMapper) {
        this.tradeProducer = tradeProducer;
        this.objectMapper = objectMapper;
    }

      public void processAndPublishTrade(String tradeId,String symbol, int quantity, double price, String side, String traderId) {

          TradeRequest tradeRequest= new TradeRequest();
          tradeRequest.setCorrelationId(UUID.randomUUID().toString());
          tradeRequest.setTimestamp(String.valueOf(LocalDateTime.now()));
          Trade trade = new Trade();
          trade.setTradeId(tradeId);
          trade.setSymbol(symbol);
          trade.setQuantity(quantity);
          trade.setPrice(BigDecimal.valueOf(price));
          trade.setSide(side);
          trade.setTraderId(traderId);

          tradeRequest.setPayload(trade);
          tradeProducer.sendTrade(tradeRequest);
      }



}
