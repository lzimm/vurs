package net.vurs.conn.sql.operation;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.Operation;
import net.vurs.transaction.Transaction;

public abstract class SQLOperation<T extends SQLBackedDefinition, R, V> extends Operation<R, V> {

	protected SQLBackedEntityManager<T> manager = null;
	
	public SQLOperation(SQLBackedEntityManager<T> manager,
			Transaction transaction) {
		super(transaction);
		this.manager = manager;
	}

}
