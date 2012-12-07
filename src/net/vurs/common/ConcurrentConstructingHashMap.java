package net.vurs.common;

import java.util.concurrent.ConcurrentHashMap;

import net.vurs.common.functional.F1;

public class ConcurrentConstructingHashMap<K, V> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 6779620312037661117L;
	
	private F1<K, V> constructor;
	
	public ConcurrentConstructingHashMap(F1<K, V> constructor) {
		this.constructor = constructor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		V ret = super.get(key);
		if (ret == null) {
			ret = this.constructor.f((K) key);
			V existing = super.putIfAbsent((K) key, ret);
			if (existing != null) {
				ret = existing;
			}
		}
		return ret;
	}
	
}