package com.rayeye.event.store;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.rayeye.event.Event;
import com.rayeye.event.EventBus;
import com.rayeye.event.EventHandler;

public abstract class AbstractEventBus implements EventBus {

	protected StoreEventBus store;
	protected MonitorEventBus read;
	protected GoogleEventBus eventBus;

	public AbstractEventBus() {
		this.init();
	}

	protected abstract void run(EventBus eventBus);

	protected abstract void put(StoreEvent event);

	private void init() {
		this.eventBus = new GoogleEventBus();
		this.store = new StoreEventBus();
		this.eventBus.on(this.store);
		this.read = new MonitorEventBus();
		this.eventBus.on(this.read);
	}

	public int post(Event event) {
		if (this.store != null) {
			this.store.post(event);
		}
		return 0;
	}

	@Override
	public void on(EventHandler handler) {
		if (this.read != null) {
			this.read.on(handler);
		}
	}

	@Override
	public void monitor() {
		if (this.read != null) {
			this.read.monitor();
		}
	}

	private class GoogleEventBus {
		private com.google.common.eventbus.EventBus eventBus;

		public GoogleEventBus() {
			this.eventBus = new com.google.common.eventbus.EventBus();
		}

		public void post(Event event) {
			this.eventBus.post(event);
		}

		public void on(Object handler) {
			this.eventBus.register(handler);
		}
	}

	private class StoreEventBus {
		public void post(Event event) {
			eventBus.post(new StoreEvent(event));
		}

		@Subscribe
		public void handler(StoreEvent event) {
			put(event);
		}
	}

	private class MonitorEventBus implements EventBus {
		private Multimap<String, EventHandler> handlers;
		private final ReadWriteLock lock = new ReentrantReadWriteLock();

		public MonitorEventBus() {
			this.handlers = HashMultimap.create();
		}

		public void monitor() {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						AbstractEventBus.this.run(MonitorEventBus.this);
					}
				}
			});
			
//			thread.setDaemon(true);
			thread.start();
		}

		public int post(Event event) {
			eventBus.post(event);
			return 0;
		}

		@Override
		public void on(EventHandler handler) {
			Method[] methods = handler.getClass().getMethods();
			lock.readLock().lock();
			try {
				for (Method m : methods) {
					if (m.getName().equals("on")) {
						Invokable invokable = Invokable.from(m);
						ImmutableList<Parameter> parameters = invokable
								.getParameters();
						if (parameters.size() > 0) {
							Parameter param = parameters.get(0);
							String typeName = param.getType().getType()
									.getTypeName();
							this.handlers.put(typeName, handler);
							break;
						}
					}
				}
			} finally {
				lock.readLock().unlock();
			}
		}

		@Subscribe
		public void readHandler(ReadEvent event) {
			if (handlers.containsKey(event.getType())) {
				Collection<EventHandler> typeHandlers = handlers.get(event.getType());
				for (EventHandler h : typeHandlers) {
					lock.readLock().lock();
					try {
						h.on(event.getEvent());
					} finally {
						lock.readLock().unlock();
					}
				}
			}
		}
	}

}
