package com.rayeye.redis;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public interface Command {

	void set(String key, Object value);

	Map<String, Object> get(String key);

	<T> T get(String key, Type type);
	
	/**
	 * 为key 设置过期时间.
	 * @param key
	 * @param value
	 * @param seconds
	 */
	void set(String key, Object value , int seconds);
	
	/**
	 * 读取key 并且设置过期时间
	 * @param key
	 * @param value
	 * @param seconds
	 */
	<T> T get(String key, Type type , int seconds);
	
	
	/**
	 * 入队  入堆
	 * @param key
	 * @param values   集合
	 * @return
	 */
	<T> long push(String key, Collection<T> values);

	
	/**
	 * 入队  入堆
	 * @param key
	 * @param value 
	 * @return
	 */
	long push(String key, Object value);

	/**
	 * 队列 （先进先出）
	 * 
	 * @param key
	 * @return
	 */
	Map<String, Object> pop(String key);

	/**
	 * 队列 （先进先出）
	 * 
	 * @param key
	 * @return
	 */
	<T> T pop(String key, Type type);

	/**
	 * 堆 (后进先出)
	 * 
	 * @param key
	 * @return
	 */
	Map<String, Object> rpop(String key);

	/**
	 * 堆 (后进先出)
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	<T> T rpop(String key, Type type);
}
