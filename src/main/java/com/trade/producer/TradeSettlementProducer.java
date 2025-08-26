package com.trade.producer;


import com.trade.exception.KafkaErrorHandlerConfig;
import com.trade.exception.KafkaUnavailableException;
import com.trade.request.TradeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TradeSettlementProducer {
    private static final String TOPIC = "settlement-events";

    private final KafkaTemplate<String, TradeRequest> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);


    public TradeSettlementProducer(KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(TradeRequest payload) {
        CompletableFuture<SendResult<String, TradeRequest>> future = kafkaTemplate
                .send("settlement-events", payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.info("Unable to send message=[" +
                        payload + "] due to : " + ex.getMessage());
                throw new KafkaUnavailableException("Kafka is down - cannot publish message",ex);


            } else {

                logger.info("Sent message=[" + payload +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");

            }
        });
    }
}

