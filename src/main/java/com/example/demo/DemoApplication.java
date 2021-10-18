package com.example.demo;

import com.example.demo.config.PairTransformerSupplier;
import mx.klar.kafka.spring.config.KafkaConfigurationProperties;
import mx.klar.kafka.spring.config.KafkaConsumerConfig;
import mx.klar.kafka.spring.config.KafkaProducerConfig;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableKafkaStreams
@EnableKafka
@SpringBootApplication
@ComponentScan(excludeFilters  = {@ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE, classes = {


		})})
public class DemoApplication {

	@Value("${local.stream.store}")
	private String stateStoreName;

	@Bean
	public PairTransformerSupplier<String> pairTransformerSupplier() {
		return new PairTransformerSupplier<>(stateStoreName);
	}


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
