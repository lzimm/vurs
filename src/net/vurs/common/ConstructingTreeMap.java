package net.vurs.common;

import java.util.TreeMap;

import net.vurs.common.functional.F1;

public class ConstructingTreeMap<K, V> extends TreeMap<K, V> {
	private static final long serialVersionUID = -4145655448806101415L;
	
	private F1<K, V> constructor;
	
	public ConstructingTreeMap(F1<K, V> constructor) {
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
