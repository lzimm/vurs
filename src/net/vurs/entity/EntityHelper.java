package net.vurs.entity;

import java.util.ArrayList;
import java.util.List;

import net.vurs.entity.typesafety.FieldKey;

public class EntityHelper<T extends Definition<?>> {
	
	public <K> List<K> getValues(FieldKey<K> key, List<Entity<T>> entities) {
		List<K> ret = new ArrayList<K>(entities.size());
		
		for (Entity<T> e: entities) {
			ret.add(e.get(key));
		}
		
		return ret;
	}
	
	public List<String> getKeys(List<Entity<T>> entities) {
		List<String> ret = new ArrayList<String>(entities.size());
		
		for (Entity<T> e: entities) {
			ret.add(e.getKey());
		}
		
		return ret;
	}

}
