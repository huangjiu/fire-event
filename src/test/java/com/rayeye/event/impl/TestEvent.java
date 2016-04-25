package com.rayeye.event.impl;

import java.util.UUID;

import com.rayeye.event.Event;

public class TestEvent implements Event {
	
	private String id;
	private String value;

	public TestEvent() {
	}

	public TestEvent(String val){
		this.value = val;
		this.id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return this.id;
	}

	public Object getValue() {
		return this.value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
