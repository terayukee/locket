package com.locket.payment.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.locket.payment.event.PaymentSuccessEvent;

@EnableKafka
@Configuration
public class KafkaConfig {

    private final Environment env;

    KafkaConfig(Environment environment) {
        this.env = environment;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);  // ✅ StringSerializer 지정
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // ✅ JsonSerializer 명시
        return props;
    }

    @Bean
    public ProducerFactory<String, PaymentSuccessEvent> producerFactory() {
       return new DefaultKafkaProducerFactory<>(this.producerConfig());
    }

    @Bean
    public KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate() {
        return new KafkaTemplate<>(this.producerFactory());
    }
}
