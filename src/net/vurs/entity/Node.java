package net.vurs.entity;

import java.util.HashMap;
import java.util.Map;


public class Node<T extends Definition<?>> {
	
	protected T def;
	protected Manager<T> manager;
	protected Map<String, Object> map = null;
	protected Map<String, Object> dirty = null;
	
	public Node(T def, Manager<T> manager, Map<String, Object> map) {
		this.def = def;
		this.manager = manager;
		this.map = map;
		this.mark();
	}
	
	public void mark() {
		this.dirty = new HashMap<String, Object>();
	}

	public Map<String, Object> getMap() { return this.map; }
	
	public void setProperty(String name, Object value) {
		this.dirty.put(name, value);
		this.map.put(name, value);
	}

	public Object getDirty(String name) {
		return this.dirty.get(name);
	}
	
	public Object getProperty(String name) {
		return getProperty(name, null);
	}
	
	public Object getProperty(String name, Object defaultValue) {
		Object ret = this.dirty.get(name);
		if (ret == null) ret = this.map.get(name);
		if (ret == null) ret = defaultValue;
		return ret;
	}
	
	public T getDef() {
		return this.def;
	}
	
	public Manager<T> getManager() {
		return this.manager;
	}

}
