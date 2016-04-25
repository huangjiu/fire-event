package com.rayeye.event.store;

import com.alibaba.fastjson.JSON;
import com.rayeye.event.Event;

public class StoreEvent implements Event {

	private String id;
	private String value;
	private String type;

	public StoreEvent(String id, String event, String type) {
		this.id = id;
		this.value = event;
		this.type = type;
	}

	public StoreEvent() {
	}

	public StoreEvent(Event event) {
		this(event.getId(), JSON.toJSONString(event), event.getClass()
				.getName());
	}

	public Event getEvent() throws ClassNotFoundException {
		return (Event) JSON.parseObject(this.getValue(),
				Class.forName(getType()));
	}

	public String getId() {
		return this.id;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}

}
