package com.trade.dto;

import com.trade.request.TradeRequest;

import java.util.Date;

public class TradeOutboxDto {
    String aggregate_id;
    String event_type;
    TradeRequest event_data;
    Date created_at;
    Date processed_at;
    boolean processed;
}
