package com.rayeye.redis;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RedisByteCommand implements Command {

	private Jedis jedis;
	
	public RedisByteCommand(Jedis jedis){
		this.jedis = jedis;
	}
	
	@Override
	public void set(String key, Object value) {
		jedis.set(stringToBytes(key), objectToBytes(value));
	}

	@Override
	public void set(String key, Object value, int seconds) {
		byte[] byteKey = stringToBytes(key);
		jedis.set(byteKey , objectToBytes(value));
		jedis.expire(byteKey, seconds);
	}
	
	@Override
	public Map<String, Object> get(String key) {
		byte[] bytes = jedis.get(stringToBytes(key));
		return (JSONObject) parse(bytes);
	}

	@Override
	public <T> T get(String key, Type type) {
		byte[] bytes = jedis.get(stringToBytes(key));
		return parse(bytes, type);
	}
	
	@Override
	public <T> T get(String key, Type type, int seconds) {
		byte[] byteKey = stringToBytes(key);
		byte[] bytes = jedis.get(stringToBytes(key));
		jedis.expire(byteKey, seconds);
		return parse(bytes, type);
	}
	
	
	@Override
	public <T> long push(String key, Collection<T> values) {
		long result = 0;
		for (T t : values) {
			result += push(key, t);
		}
		return result;
	}

	@Override
	public long push(String key, Object value) {
		return jedis.lpush(stringToBytes(key), objectToBytes(value));
	}

	@Override
	public Map<String, Object> pop(String key) {
		byte[] bytes = jedis.rpop(stringToBytes(key));
		return (JSONObject) parse(bytes);
	}

	@Override
	public <T> T pop(String key, Type type) {
		byte[] bytes = jedis.rpop(stringToBytes(key));
		return parse(bytes, type);
	}
	
	@Override
	public Map<String, Object> rpop(String key) {
		byte[] bytes = jedis.lpop(stringToBytes(key));
		return (JSONObject) parse(bytes);
	}
	
	@Override
	public <T> T rpop(String key, Type type) {
		byte[] bytes = jedis.lpop(stringToBytes(key));
		return parse(bytes, type);
	}
	

	private byte[] stringToBytes(String str) {
		return str.getBytes();
	}

	private byte[] objectToBytes(Object value) {
		return JSON.toJSONBytes(value);
	}

	private Object parse(byte[] bytes) {
		if( bytes == null) {
			return null;
		}
		return JSON.parse(bytes);
	}

	private <T> T parse(byte[] bytes, Type type) {
		if( bytes == null) {
			return null;
		}
		return JSON.parseObject(bytes, type);
	}

}