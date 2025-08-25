package com.trade.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.dto.KafkaMessage;
import com.trade.dto.Trade;
import com.trade.entity.TradeSettlementOutBoxEntity;
import com.trade.producer.TradeSettlementProducer;
import com.trade.repository.TradeSettlementOutBoxRepo;
import com.trade.request.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
public class TradeSettlementPublisherService {

    @Autowired
    private TradeSettlementOutBoxRepo repository;

    @Autowired
    TradeSettlementProducer settlementProducer;

    @Autowired
    TradeSettlementProducer tradeSettlementService;



    private final ObjectMapper objectMapper;

    public TradeSettlementPublisherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Scheduled(fixedRate = 60000)
    public void TradeOutboxPublish() {

        List<TradeSettlementOutBoxEntity> unProcessedRecords= repository.findByStatusFalse();


        unProcessedRecords.forEach(outbox -> {
            try {

                KafkaMessage<Trade> kafkaMessage = objectMapper.readValue(
                        outbox.getPayload(), new TypeReference<KafkaMessage<Trade>>() {}
                );

                System.out.println("kafkaMessage :"+kafkaMessage.getCorrelationId());
                System.out.println("kafkaMessage payload :"+kafkaMessage.getPayload());
                Trade trade = new Trade();
                // trade.setTradeId(outbox.);
                TradeRequest request = new TradeRequest();
                request.setCorrelationId(kafkaMessage.getCorrelationId());
                request.setPayload(kafkaMessage.getPayload());
                tradeSettlementService.publish(request);
                outbox.setStatus(true);
                repository.save(outbox);

            } catch (Exception ignored) {
                //  lo.error(ignored.getMessage());
            }
        });

    }

}
