package net.vurs.common;

import java.util.HashMap;

import net.vurs.common.functional.F1;

public class ConstructingHashMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = -2667035668267046955L;
	
	private F1<K, V> constructor;
	
	public ConstructingHashMap(F1<K, V> constructor) {
		this.constructor = constructor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V ret = super.get(key);
		if (ret == null) {
			ret = this.constructor.f((K) key);
			super.put((K) key, ret);
		}
		return ret;
	}
	
}
