package com.rayeye.event.redis;

import redis.clients.jedis.Jedis;

import com.rayeye.event.EventBus;
import com.rayeye.event.store.AbstractEventBus;
import com.rayeye.event.store.ReadEvent;
import com.rayeye.event.store.StoreEvent;
import com.rayeye.redis.Command;
import com.rayeye.redis.RedisByteCommand;

public class RedisEventBus extends AbstractEventBus {

	private Command command;
	private final String EVENTS_KEY = "events";
	private Jedis redis;
	
	public RedisEventBus(String host , int port) {
		super();
		this.redis = new Jedis(host , port);
		this.command = new RedisByteCommand(new Jedis(host , port));
	}

	public synchronized void put( StoreEvent event){
		command.push( EVENTS_KEY , event);
		this.redis.publish(EVENTS_KEY , event.getId());
	}
	
	private synchronized StoreEvent get(){
		StoreEvent event = command.pop(EVENTS_KEY , StoreEvent.class);
		return event;
	}

	@Override
	public void run(EventBus eventBus) {
		StoreEvent event = null;
		try {
			if( ( event = this.get() ) != null ) {
				eventBus.post(new ReadEvent(event.getType(), event.getEvent() ));
			}
		} catch (ClassNotFoundException e) {
			//nothing
		}
	}
	
	private void executeEvents(EventBus eventBus) {
		StoreEvent event = null;
		while(( event = this.get() ) != null ) {
			try {
				eventBus.post(new ReadEvent(event.getType(), event.getEvent() ));
			} catch (ClassNotFoundException e) {
				//nothing
			}
		}
	}
	
	@Override
	public void monitor() {
		if( this.redis == null ) {
			return;
		}
		
		redis.subscribe(new RedisEventListener(){
			@Override
			public void onMessage(String channel, String message) {
				RedisEventBus.this.run(read);
			}
			
			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				RedisEventBus.this.executeEvents(read);
			}
		}, EVENTS_KEY);		
	}
	
}
