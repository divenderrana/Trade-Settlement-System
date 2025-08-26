package com.trade.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

@RestControllerAdvice
public class TradeSettlementGlobalExceptionHandler {

    @ExceptionHandler(KafkaUnavailableException.class)
    public ResponseEntity<String> handleKafkaUnavailable(KafkaUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Kafka service is currently unavailable. Please retry later.");
    }
    @ExceptionHandler(KafkaMessageFormatException.class)
    public ResponseEntity<String> handleKafkaFormat(KafkaMessageFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid Kafka message format: " + ex.getMessage());
    }
}
