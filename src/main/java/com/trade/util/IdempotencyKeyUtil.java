package com.trade.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.trade.dto.Trade;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class IdempotencyKeyUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public static String generateIdempotencyKey(Trade trade) {

        try {
            String json = mapper.writeValueAsString(trade);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] tradeByte = messageDigest.digest(json.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tradeByte);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate idempotency key", e);

        }

    }
}
