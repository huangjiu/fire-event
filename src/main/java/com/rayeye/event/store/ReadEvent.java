package com.rayeye.event.store;

import com.rayeye.event.Event;

public class ReadEvent implements Event {
	
	private String type;
	private Event event;
	
	public ReadEvent(String type , Event event) {
		this.type = type;
		this.event = event;
	}

	public String getType() {
		return type;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String getId() {
		return null;
	}
}
