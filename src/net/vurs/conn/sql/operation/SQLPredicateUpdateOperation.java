package net.vurs.conn.sql.operation;

import net.vurs.common.Triple;
import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.operation.params.SQLValueList;
import net.vurs.conn.sql.operation.predicate.SQLPredicate;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.transaction.Transaction;

public class SQLPredicateUpdateOperation<T extends SQLBackedDefinition> extends SQLOperation<T, Boolean, Integer> {
	
	private SQLPredicate<T> predicate = null;
	private SQLValueList<T> values = null;
	
	public SQLPredicateUpdateOperation(SQLBackedEntityManager<T> manager, Transaction transaction, SQLValueList<T> values, SQLPredicate<T> predicate) {
		super(manager, transaction);
		this.predicate = predicate;
		this.values = values;
	}

	@Override
	protected Triple<Boolean, Boolean, Integer> op() {
		SQLConnection conn = this.manager.acquireConnection();
		
		int modifications = 0;
		
		try {
			StringBuilder query = new StringBuilder(this.manager.updateStart())
											.append(this.values.stream(this.manager))
											.append(" WHERE ")
											.append(this.predicate.stream(this.manager));
			
			modifications = conn.update(query.toString());
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Integer>(modifications > 0, modifications > 0, modifications);
	}
	
}
