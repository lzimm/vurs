package net.vurs.entity.typesafety;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.annotation.Prefetch;

public class FieldKey<T> extends Key<T> {

	public static enum FetchType {
		NONE, ENTITY, LIST
	}
	
	private FetchType prefetch = FetchType.NONE;
	
	public FieldKey(Class<? extends Definition<?>> parent, Class<T> type, ParameterizedType subType, String name, Annotation[] annotations) {
		super(parent, type, subType, name);
		
		for (Annotation a: annotations) {
			if (a.annotationType().equals(Prefetch.class)) {
				if (Entity.class.isAssignableFrom(type)) {
					this.prefetch = FetchType.ENTITY;
				} else if (List.class.isAssignableFrom(type)) {
					this.prefetch = FetchType.LIST;
				}
			}
		}
	}
	
	public FetchType prefetch() { return this.prefetch; }

}
