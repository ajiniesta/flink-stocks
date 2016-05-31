package com.iniesta.stocks;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaMessageHandler {

	void call(ConsumerRecord<String, String> record);

}
