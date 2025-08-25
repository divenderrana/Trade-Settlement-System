package com.trade.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.consumer.TradeConsumer;
import com.trade.dto.KafkaMessage;
import com.trade.request.TradeRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@Component

public class SendToDeadLetterQueueProducer {

    private final ObjectMapper objectMapper;
    @Value("${app.kafka.topics.dlq}")
    private String dlqTopic;
    private final KafkaTemplate<String, TradeRequest> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SendToDeadLetterQueueProducer.class);

    public SendToDeadLetterQueueProducer(ObjectMapper objectMapper, KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendToDeadLetterQueue(TradeRequest tradeRequest, String errorReason, ConsumerRecord<String, String> record) {
        try {
            KafkaMessage<TradeRequest> dlqMessage = new KafkaMessage<>();
            dlqMessage.setMessageId(UUID.randomUUID().toString());
            dlqMessage.setCorrelationId(record.key());
            dlqMessage.setMessageType("DLQ_MESSAGE");
            dlqMessage.setPayload(tradeRequest);
            dlqMessage.setHeaders(Map.of(
                    "originalTopic", record.topic(),
                    "originalPartition", String.valueOf(record.partition()),
                    "originalOffset", String.valueOf(record.offset()),
                    "errorReason", errorReason,
                    "failureTimestamp", String.valueOf(System.currentTimeMillis())
            ));
            dlqMessage.setTimestamp(LocalDateTime.now());

            String dlqPayload = objectMapper.writeValueAsString(dlqMessage);

            kafkaTemplate.send(dlqTopic, tradeRequest.getPayload().getTradeId(), tradeRequest);
            logger.info("Sent message to DLQ: {}", errorReason);

        } catch (Exception e) {
            logger.error("Failed to send message to DLQ", e);
        }
    }

}
