package com.trade.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.dto.KafkaMessage;
import com.trade.dto.Trade;
import com.trade.dto.TradeStatus;
import com.trade.entity.IdempotencyKey;
import com.trade.entity.TradeEntity;
import com.trade.entity.TradeSettlementOutBoxEntity;
import com.trade.exception.KafkaMessageFormatException;
import com.trade.exception.KafkaUnavailableException;
import com.trade.mapper.TradeMapper;
import com.trade.mapper.TradeOutBoxMapper;
import com.trade.producer.SendToDeadLetterQueueProducer;
import com.trade.repository.IdempotencyKeyRepository;
import com.trade.repository.TradeRepo;
import com.trade.repository.TradeSettlementOutBoxRepo;
import com.trade.request.TradeRequest;

import com.trade.util.IdempotencyKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class TradeConsumer {
    private final ObjectMapper objectMapper;

    private final SendToDeadLetterQueueProducer sendToDeadLetterQueue;

    private final KafkaTemplate<String, TradeRequest> tradeSettlementProducer;
    @Autowired
    TradeRepo tradeRepo;

    @Autowired
    TradeSettlementOutBoxRepo outBoxRepo;

    @Autowired
    private IdempotencyKeyRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(TradeConsumer.class);

    @Value("${app.kafka.max-retry-attempts}")
    private int maxRetryAttempts;
    @Value("${app.kafka.topics.dlq}")
    private String dlqTopic;

    @Autowired
    public TradeConsumer(ObjectMapper objectMapper, SendToDeadLetterQueueProducer sendToDeadLetterQueue, KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.sendToDeadLetterQueue = sendToDeadLetterQueue;
        this.tradeSettlementProducer = kafkaTemplate;
    }


    @KafkaListener(topics = "trade-events", groupId = "trade-group", containerFactory = "tradeKafkaListenerFactory")
    @Transactional
    public void consumeTrade(TradeRequest tradeRequest, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Header(KafkaHeaders.OFFSET) long offset, ConsumerRecord<String, String> record, Acknowledgment acknowledgment) throws JsonProcessingException {

        String messageId = null;
        String correlationId = null;

        try {
            Trade trade = tradeRequest.getPayload();
            String idempotencyKey = IdempotencyKeyUtil.generateIdempotencyKey(trade);

            Optional<IdempotencyKey> repoKey = repository.findByIdempotencyKey(idempotencyKey);

            if (!repoKey.isPresent()) {
                repository.save(new IdempotencyKey(idempotencyKey));


            }
            logger.info("Received Trade Message: " + tradeRequest);
            LocalDate currentDate = LocalDate.now();


            correlationId = tradeRequest.getCorrelationId();

            if (correlationId == null || correlationId.trim().isEmpty()) {
                logger.warn("Received request without correlation ID, sending to DLQ");
                sendToDeadLetterQueue.sendToDeadLetterQueue(tradeRequest, "Missing correlation ID", record);
                acknowledgment.acknowledge();
                return;
            } else {

                trade.setStatus(TradeStatus.SETTLED);
                TradeEntity tradeEntity = TradeMapper.toEntity(trade);
                tradeRepo.save(tradeEntity);
                TradeOutBoxMapper tradeOutBoxMapper = new TradeOutBoxMapper(objectMapper);
                TradeSettlementOutBoxEntity outBoxEntity = tradeOutBoxMapper.toEntity(tradeRequest);
                outBoxRepo.save(outBoxEntity);
                acknowledgment.acknowledge();

            }
        }  catch (KafkaUnavailableException ex) {
            throw new KafkaUnavailableException("Kafka consumer failed", ex);
        }
        catch (KafkaMessageFormatException ex) {
            throw new KafkaMessageFormatException("Invalid message format", ex);}
        }


    public static boolean isDecimal(BigDecimal number) {
        return number.stripTrailingZeros().scale() > 0;
    }

    private void handleProcessingFailure(TradeRequest tradeRequest, String correlationId, ConsumerRecord<String, String> record, Exception exception, Acknowledgment acknowledgment) {
        try {


            Trade request = tradeRequest.getPayload();
            int currentRetryCount = tradeRequest.getRetryCount();

            if (currentRetryCount + 1 >= maxRetryAttempts) {
                sendToDeadLetterQueue.sendToDeadLetterQueue(tradeRequest, "Processing failed after max retries: " + exception.getMessage(), record);
                acknowledgment.acknowledge();
            } else {
                tradeRequest.setRetryCount(currentRetryCount + 1);
                tradeRequest.setPayload(request);
                String retryMessage = objectMapper.writeValueAsString(tradeRequest);
                acknowledgment.acknowledge();
            }

        } catch (Exception e) {
            logger.error("Failed to handle processing failure for message", e);
        }


    }

}