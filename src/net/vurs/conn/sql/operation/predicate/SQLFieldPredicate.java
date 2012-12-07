package net.vurs.conn.sql.operation.predicate;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.typesafety.Key;

public class SQLFieldPredicate<T extends SQLBackedDefinition, K> extends SQLPredicate<T> {

	public static enum Comparator {
		EQ(" = "), 
		LT(" < "), 
		GT(" > "), 
		NE(" != "),
		LIKE(" LIKE ");
		
		Comparator(String op) {
			this.op = op;
		}
		
		private String op = null;
		private String getOp() { return this.op; }
	}
	
	private Key<K> key = null;
	private K val = null;
	private Comparator comparator = null;
	
	public SQLFieldPredicate(Key<K> key, K val) {
		this(key, Comparator.EQ, val);
	}

	public SQLFieldPredicate(Key<K> key, Comparator comparator, K val) {
		this.key = key;
		this.val = val;
		this.comparator = comparator;
	}

	@Override
	public String stream(SQLBackedEntityManager<T> manager) {
		StringBuilder streamed = new StringBuilder().append('`')
									.append(this.key.getName())
									.append('`')
									.append(this.comparator.getOp())
									.append('\'');
		
		streamed.append(stream(manager, this.key, this.val));
		
		streamed.append('\'');
		
		return streamed.toString();
	}
	
}
