package com.locket.user.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.locket.kafka.event.PaymentSuccessEvent;

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
		deserializer.addTrustedPackages("*");  // ✅ 모든 패키지 신뢰
	    deserializer.setRemoveTypeHeaders(true);  // ✅ @class 정보 제거
		deserializer.setUseTypeMapperForKey(false);  // ✅ Key는 기본적으로 String이므로 매핑 비활성화

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
		return factory;
	}
}
