package com.trade.exception;

public class KafkaMessageFormatException extends RuntimeException {
    public KafkaMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
