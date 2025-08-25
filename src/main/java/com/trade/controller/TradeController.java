package com.trade.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.trade.consumer.TradeConsumer;
import com.trade.services.TradeSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeSettlementService tradeService;
    @Autowired
    TradeConsumer tradeConsumer;

    public TradeController(TradeSettlementService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public String createTrade(@RequestParam String tradeId, @RequestParam String symbol, @RequestParam int quantity, @RequestParam double price, @RequestParam String side, @RequestParam String traderId) throws JsonProcessingException {

        tradeService.processAndPublishTrade(tradeId, symbol, quantity, price, side, traderId);


        return "✅ Trade published successfully!";
    }
}

