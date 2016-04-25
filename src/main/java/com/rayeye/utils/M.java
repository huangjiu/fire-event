package com.rayeye.utils;

import java.util.HashMap;

public class M<K , V>  extends HashMap<K, V>{
	
	public static <K , V> M<K , V> ins(){
		return new M<K , V>();
	}
	
	public M<K , V> put2(K k , V v ){
		this.put(k, v);
		return this;
	}
}
