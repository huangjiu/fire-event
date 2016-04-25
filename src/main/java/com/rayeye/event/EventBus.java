package com.rayeye.event;

public interface EventBus {
	
	public int post(Event event);
	
	public void on(EventHandler handler);
	
	public void monitor();
	
}
