package net.vurs.conn.sql.operation.predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.Key;

public class SQLInPredicate<T extends SQLBackedDefinition, K> extends SQLPredicate<T> {

	private Key<K> key = null;
	private List<K> vals = null;
	
	public SQLInPredicate(Key<K> key) {
		this.key = key;
		this.vals = new ArrayList<K>();
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Definition<?>> SQLInPredicate(Key<K> key, List<Entity<E>> entities) {
		this.key = key;
		this.vals = new ArrayList<K>(entities.size());
		
		for (Entity<E> entity: entities) {
			this.vals.add((K) entity.getKey());
		}
	}
	
	public <E extends Definition<?>> SQLInPredicate(Key<K> key, FieldKey<K> col, List<Entity<E>> entities) {
		this.key = key;
		this.vals = new ArrayList<K>(entities.size());
		
		for (Entity<E> entity: entities) {
			this.vals.add(entity.get(col));
		}
	}

	public void add(K val) {
		this.vals.add(val);
	}

	@Override
	public String stream(SQLBackedEntityManager<T> manager) {
		StringBuilder streamed = new StringBuilder().append('`')
										.append(this.key.getName())
										.append("` IN (");
		
		Iterator<K> itr = this.vals.iterator();
		while (itr.hasNext()) {
			K val = itr.next();
			streamed.append('\'').append(stream(manager, this.key, val)).append('\'');
			
			if (itr.hasNext()) {
				streamed.append(',');
			}
		}
		
		streamed.append(')');
		
		return streamed.toString();
	}
	
}