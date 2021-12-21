package cn.pioneeruniverse.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class MessageAdapterConfig {

	@Resource(name="messageHandler")
	private MessageHandler messageHandler;
	
	@Bean
	public MessageListenerAdapter sendEmailAdapter(){
		return new MessageListenerAdapter(messageHandler,"sendEmailMessage");
	}
	
	@Bean
	public MessageListenerAdapter sendWeChatAdapter(){
		return new MessageListenerAdapter(messageHandler,"sendWeChatMessage");
	}
	

}
