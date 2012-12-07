package net.vurs.conn.sql.operation;

import net.vurs.common.Triple;
import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.transaction.Transaction;

public class SQLDeleteOperation<T extends SQLBackedDefinition> extends SQLOperation<T, Boolean, Void> {

	protected String key = null;
	
	public SQLDeleteOperation(SQLBackedEntityManager<T> manager, Transaction transaction, String key) {
		super(manager, transaction);
		this.key = key;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Void> op() {		
		SQLConnection conn = this.manager.acquireConnection();
		
		boolean success = false;
		
		try {
			StringBuilder query = new StringBuilder(this.manager.deleteStart())
												.append(key)
												.append('\'');

			success = conn.update(query.toString()) > 0;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}

}
