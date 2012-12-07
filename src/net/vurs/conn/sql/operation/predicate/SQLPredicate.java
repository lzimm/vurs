package net.vurs.conn.sql.operation.predicate;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.typesafety.Key;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.util.EscapeUtil;
import net.vurs.util.Serialization;

public abstract class SQLPredicate<T extends SQLBackedDefinition> {

	public abstract String stream(SQLBackedEntityManager<T> manager);
	
	protected <K> String stream(SQLBackedEntityManager<T> manager, Key<K> key, K value) {
		String streamed = null;
		
		if (PrimaryKey.class.isAssignableFrom(key.getClass())) {
			streamed = manager.getStreamer().getPrimaryStreamer()
						.write(value, Serialization.serialize(key.getName()), System.currentTimeMillis());
		} else {
			streamed = manager.getStreamer().getStreamer(key.getName())
						.write(value, Serialization.serialize(key.getName()), System.currentTimeMillis());
		}
		
		return EscapeUtil.escape(streamed, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS);
	}
	
	public SQLPredicate<T> and(SQLPredicate<T> predicate) {
		return new SQLAndPredicate<T>(this, predicate);
	}
	
	@SuppressWarnings("rawtypes")
	public SQLPredicate<T> andAll(SQLPredicate... predicates) {
		return new SQLAndPredicate<T>(this, new SQLAndPredicate<T>(predicates));
	}
	
	@SuppressWarnings("rawtypes")
	public SQLPredicate<T> andAny(SQLPredicate... predicates) {
		return new SQLAndPredicate<T>(this, new SQLOrPredicate<T>(predicates));
	}
	
	public SQLPredicate<T> or(SQLPredicate<T> predicate) {
		return new SQLOrPredicate<T>(this, predicate);
	}
	
	@SuppressWarnings("rawtypes")
	public SQLPredicate<T> orAll(SQLPredicate... predicates) {
		return new SQLOrPredicate<T>(this, new SQLAndPredicate<T>(predicates));
	}
	
	@SuppressWarnings("rawtypes")
	public SQLPredicate<T> orAny(SQLPredicate... predicates) {
		return new SQLOrPredicate<T>(this, new SQLOrPredicate<T>(predicates));
	}
	
	public <K> SQLPredicate<T> and(Key<K> key, K value) {
		return new SQLAndPredicate<T>(this, new SQLFieldPredicate<T, K>(key, value));
	}
	
	public <K> SQLPredicate<T> or(Key<K> key, K value) {
		return new SQLOrPredicate<T>(this, new SQLFieldPredicate<T, K>(key, value));
	}
	
	public SQLPredicate<T> limit(int limit) {
		return new SQLLimitPredicate<T>(this, limit);
	}

}
