package com.trade.exception;

public class KafkaUnavailableException extends RuntimeException {
    public KafkaUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
