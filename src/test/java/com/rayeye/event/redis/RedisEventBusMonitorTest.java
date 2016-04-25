package com.rayeye.event.redis;

import redis.clients.jedis.Jedis;

import com.rayeye.event.EventHandler;
import com.rayeye.event.impl.TestEvent;
import com.rayeye.event.impl.TestEvent2;

public class RedisEventBusMonitorTest {


	public static void main(String[] args){
		
		final Jedis jedis = new Jedis("127.0.0.1" , 6379);
		RedisEventBus eventBus = new RedisEventBus("127.0.0.1" , 6379);
		eventBus.on(new EventHandler<TestEvent>() {
			@Override
			public void on(TestEvent event) {
				System.out.println("event1:" +  event.getId());
			}
		});
		
		eventBus.on(new EventHandler<TestEvent2>() {
			public void on(TestEvent2 event) {
				System.out.println("event2:" +  event.getId());
			}
		});
		eventBus.monitor();
	}

}
