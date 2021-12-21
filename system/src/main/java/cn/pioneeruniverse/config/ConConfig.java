package cn.pioneeruniverse.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import cn.pioneeruniverse.common.constants.Constants;

@Configuration
public class ConConfig {

	@Resource(name = "sendEmailAdapter")
	private MessageListenerAdapter sendEmailAdapter;
	
	@Resource(name = "sendWeChatAdapter")
	private MessageListenerAdapter sendWeChatAdapter;
	
	/**
	 * 创建连接工厂
	 * @param connectionFactory
	 * @param listenerAdapter
	 * @return
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		// 可以添加多个
		container.addMessageListener(sendEmailAdapter, new PatternTopic(Constants.SEND_EMAIL_MESSAGE));
		container.addMessageListener(sendWeChatAdapter, new PatternTopic(Constants.SEND_WECHAT_MESSAGE));
		return container;
	}

}
