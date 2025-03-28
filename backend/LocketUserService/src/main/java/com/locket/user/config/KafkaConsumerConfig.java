package com.locket.user.config;

import com.locket.kafka.event.PaymentSuccessEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	private final Environment env;

	KafkaConsumerConfig(Environment env) {
		this.env = env;
	}

	@Bean
	public ConsumerFactory<String, PaymentSuccessEvent> consumerFactory() {
		// ✅ JSON 역직렬화를 위한 JsonDeserializer 설정
		JsonDeserializer<PaymentSuccessEvent> deserializer = new JsonDeserializer<>(PaymentSuccessEvent.class);
//		deserializer.addTrustedPackages("*");  // ✅ 모든 패키지 신뢰
		deserializer.addTrustedPackages("com.locket.kafka.event");  // ✅ 특정 패키지만 허용
		deserializer.setRemoveTypeHeaders(false);  // ✅ Type 정보를 유지하여 직렬화 오류 방지
		deserializer.setUseTypeMapperForKey(false);

		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.bootstrap-servers"));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);


		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(this.consumerFactory());

		// ✅ ack-mode: manual_immediate 설정
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		return factory;
	}
}
