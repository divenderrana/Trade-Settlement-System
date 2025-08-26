package com.trade.exception;

import com.trade.request.TradeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);
   private final KafkaTemplate<String, TradeRequest> kafkaTemplate;

    public KafkaErrorHandlerConfig(KafkaTemplate<String, TradeRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, TradeRequest> template) {
        KafkaFuture.BiConsumer<ConsumerRecord<?, ?>, Exception> recoverer = (record, exception) -> {
            // Send to DLQ
            logger.error("Sending record to DLQ due to error: {}", exception.getMessage(), exception);
            template.send("trade-dlq", (TradeRequest) record.value());
        };

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template, (r, e) -> new TopicPartition("trade-dlq", r.partition())), new FixedBackOff(1000L, 2) // retry twice
        );

        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
