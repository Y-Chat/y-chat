package com.ychat.ychat;

import com.asyncapi.gen.notification.infrastructure.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig extends Config {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Override
    public Map<String, Object> consumerConfigs() {
        var config = super.consumerConfigs();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return config;
    }
}
