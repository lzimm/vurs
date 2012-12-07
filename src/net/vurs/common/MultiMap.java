package net.vurs.common;

import java.util.ArrayList;
import java.util.List;

import net.vurs.common.constructor.ArrayListConstructor;

public class MultiMap<K, V> extends ConstructingHashMap<K, List<V>> {
	private static final long serialVersionUID = 3082499898019807097L;

	public MultiMap() {
		super(new ArrayListConstructor<K, V>());
	}
	
	public void add(K key, V val) {
		super.get(key).add(val);
	}
	
	@Override
	public List<V> remove(Object key) {
		return super.remove(key);
	}
	
	public V first(K key) { return this.first(key, null); }
	public V first(K key, V defaultValue) {
		ArrayList<V> vals = (ArrayList<V>) super.get(key);
		return vals != null && vals.size() > 0 ? vals.get(0) : defaultValue;
	}

	public V getFirstAndRemove(K key) { return this.getFirstAndRemove(key, null); }
	public V getFirstAndRemove(K key, V defaultValue) {
		ArrayList<V> vals = (ArrayList<V>) super.remove(key);
		return vals != null && vals.size() > 0 ? vals.get(0) : defaultValue;
	}
	
}
