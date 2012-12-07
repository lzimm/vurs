package net.vurs.entity.typesafety;

import java.lang.reflect.ParameterizedType;

import net.vurs.entity.Definition;

public class Key<T> {
	
	private Class<? extends Definition<?>> parent = null;
	private Class<T> type = null;
	private ParameterizedType subType = null;
	private String name = null;

	public Key(Class<? extends Definition<?>> parent, Class<T> type, ParameterizedType subType, String name) {
		this.parent = parent;
		this.type = type;
		this.subType = subType;
		this.name = name;
	}

	public Class<? extends Definition<?>> getParent() { return this.parent; }
	public Class<T> getType() { return this.type; }
	public ParameterizedType getSubType() { return this.subType; }
	public String getName() { return this.name; }
	
}
