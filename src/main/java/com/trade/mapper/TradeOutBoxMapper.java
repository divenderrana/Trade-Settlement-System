package com.trade.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.entity.TradeSettlementOutBoxEntity;
import com.trade.request.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TradeOutBoxMapper {


    private  ObjectMapper objectMapper;

    public TradeOutBoxMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }



    public  TradeSettlementOutBoxEntity toEntity(TradeRequest request) {
        try {
            TradeSettlementOutBoxEntity outboxEvent = new TradeSettlementOutBoxEntity();
            outboxEvent.setEventId(UUID.randomUUID().toString());
            outboxEvent.setCorrelationId(request.getCorrelationId());
            outboxEvent.setTopic("settlement-events");
            outboxEvent.setEventType("settlement-events");
            outboxEvent.setPayload(objectMapper.writeValueAsString(request));
            outboxEvent.setStatus(false);

            return outboxEvent;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
