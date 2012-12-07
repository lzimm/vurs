package net.vurs.conn.sql.operation.predicate;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;

public class SQLLimitPredicate<T extends SQLBackedDefinition> extends SQLPredicate<T> {

	private SQLPredicate<T> wherePredicate = null;
	private int limit = 0;
	
	public SQLLimitPredicate(SQLPredicate<T> wherePredicate, int limit) {
		this.wherePredicate = wherePredicate;
		this.limit = limit;
	}
	
	@Override
	public String stream(SQLBackedEntityManager<T> manager) {
		StringBuilder streamed = new StringBuilder(this.wherePredicate.stream(manager)).append(" LIMIT ").append(this.limit);
		return streamed.toString();
	}

}
