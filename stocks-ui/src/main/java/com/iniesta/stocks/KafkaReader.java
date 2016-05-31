package com.iniesta.stocks;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaReader {

	private Properties props;
	private InnerThread innerThread;
	private KafkaConsumer<String, String> consumer;

	public static class InnerThread implements Runnable{

		private KafkaConsumer<String, String> consumer;
		private KafkaMessageHandler handler;
		private Boolean stop;

		public InnerThread(final KafkaMessageHandler handler, KafkaConsumer<String, String> consumer) {
			this.handler = handler;
			this.consumer = consumer;
			stop = false;
		}

		public synchronized void stop(){
			stop = true;
		}
		
		@Override
		public void run() {
			while (!stop) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					if (record != null && record.value() != null && !record.value().isEmpty()) {
						handler.call(record);
					}
				}
			}			
		}
		
	}
	
	public KafkaReader() {
		props = new Properties();
		// props.put("bootstrap.servers", "192.168.99.100:9092");
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "consumer-tutorial");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
	}
	
	public void start(KafkaMessageHandler handler){
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("reader"));
		innerThread = new InnerThread(handler, consumer);
		new Thread(innerThread, "Kafka Reader wait loop").start();;
	}
	
	public void stop(){
		innerThread.stop();
		consumer.close();
	}

	public static void main(String[] args) throws InterruptedException {
		KafkaReader kafkaReader = new KafkaReader();
		kafkaReader.start(new KafkaMessageHandler() {

			@Override
			public void call(ConsumerRecord<String, String> record) {
				System.out.println(">>>>>> " + record.value());

			}
		});
		Thread.sleep(60000);
		kafkaReader.stop();
	}
}
