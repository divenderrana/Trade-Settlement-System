package com.trade.producer;


import com.trade.request.TradeRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TradeSettlementProducer {
    private static final String TOPIC = "settlement-events";

    private final KafkaTemplate<String, TradeRequest> kafkaTemplate;

    public TradeSettlementProducer(KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTradeSettlement(TradeRequest tradeMessage) {
        kafkaTemplate.send(TOPIC, tradeMessage.getPayload().getTradeId(), tradeMessage);
        System.out.println("✅ Sent Trade Message: " + tradeMessage);
    }

    //public void send(String dlqTopic, String key, String dlqPayload) {
   // }

    public void publish(TradeRequest payload) {
        CompletableFuture<SendResult<String, TradeRequest>> future = kafkaTemplate
                .send("settlement-events", payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + payload +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        payload + "] due to : " + ex.getMessage());
            }
        });
    }
}

