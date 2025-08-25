package com.trade.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trade.dto.Trade;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeRequest {

    private Trade payload;
    private String correlationId;
    private String timestamp;

    private int retryCount;

    public Trade getPayload() {
        return payload;
    }

    public void setPayload(Trade payload) {
        this.payload = payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
