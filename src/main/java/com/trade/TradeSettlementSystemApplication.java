package com.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class TradeSettlementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeSettlementSystemApplication.class, args);
	}

}
