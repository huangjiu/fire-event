package com.rayeye.event;

public interface EventHandler<T extends Event> {
	
	public void on(T event);
	
}
