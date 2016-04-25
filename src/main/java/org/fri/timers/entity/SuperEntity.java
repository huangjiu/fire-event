package org.fri.timers.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SuperEntity  {
	
	private long created;

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
	
	

}
