package com.trade.dto;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeArrayDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int[] arr = p.readValueAs(int[].class);
        if (arr.length >= 7) {
            return LocalDateTime.of(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6]);
        } else if (arr.length >= 6) {
            return LocalDateTime.of(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
        }
        return null;
    }
}

