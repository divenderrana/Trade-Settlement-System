package com.trade.producer;


import com.trade.request.TradeRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TradeProducer {
    private static final String TOPIC = "trade-events";
    private final KafkaTemplate<String, TradeRequest> kafkaTemplate;

    public TradeProducer(KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTrade(TradeRequest tradeMessage) {
        kafkaTemplate.send(TOPIC, "", tradeMessage);
        System.out.println("✅ Sent Trade Message: " + tradeMessage);
    }
}

