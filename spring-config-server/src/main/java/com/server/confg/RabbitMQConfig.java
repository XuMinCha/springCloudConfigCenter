package com.server.confg;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * rabbitmq配置
 * author:xuemc
 */
@Configuration
public class RabbitMQConfig {

	@Bean(name="connectionFactory")
    public ConnectionFactory connectionFactory(RabbitProperties properties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        PropertyMapper map = PropertyMapper.get();
		map.from(properties::determineHost).whenNonNull().to(connectionFactory::setHost);
		map.from(properties::determinePort).to(connectionFactory::setPort);
		map.from(properties::determineUsername).whenNonNull().to(connectionFactory::setUsername);
		map.from(properties::determinePassword).whenNonNull().to(connectionFactory::setPassword);
		map.from(properties::determineVirtualHost).whenNonNull().to(connectionFactory::setVirtualHost);
		
		//let executor thread make sense
		RabbitMQThreadFactory rabbitMQThreadFactory = new RabbitMQThreadFactory("message-dispatcher-");
		int nThreads = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads,nThreads,0L,TimeUnit.MILLISECONDS,
				 							                 new LinkedBlockingQueue<Runnable>(),rabbitMQThreadFactory);
		connectionFactory.setExecutor(executor);
        return connectionFactory;
    }
	
	public static class RabbitMQThreadFactory implements ThreadFactory {
		
		private AtomicInteger threadNum = new AtomicInteger();
		
		private String namePrefix;
		
		public RabbitMQThreadFactory(String namePrefix) {
				this.namePrefix = namePrefix;
	    }

		public void setNamePrefix(String namePrefix) {
			this.namePrefix = namePrefix;
		}
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r,namePrefix + threadNum.getAndIncrement());
			if(t.isDaemon()){
				t.setDaemon(false);
			}
			if(t.getPriority() != Thread.NORM_PRIORITY){
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}
