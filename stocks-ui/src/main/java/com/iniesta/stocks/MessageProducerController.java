package com.iniesta.stocks;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducerController {

	@Autowired
	private SimpMessagingTemplate template;
	private KafkaReader kafkaReader;

	@PostConstruct
	public void postConstruct() {
		System.out.println("------------>>>>>>>>>>>. " + template);
		kafkaReader = new KafkaReader();
		kafkaReader.start(new KafkaMessageHandler() {

			@Override
			public void call(ConsumerRecord<String, String> record) {
				System.out.println(">>>>>> " + record.value());
				try{
					template.send("/hello", new Message<String>() {
						@Override
						public MessageHeaders getHeaders() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public String getPayload() {
							return record.value();
						}
					});
//					template.convertAndSend("/topics/greetings", record.value());
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		});
	}
	
	@PreDestroy
	public void preDestroy(){
		kafkaReader.stop();
	}

}
