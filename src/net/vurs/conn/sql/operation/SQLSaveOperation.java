package net.vurs.conn.sql.operation;

import net.vurs.common.Triple;
import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.SQLNode;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.transaction.Transaction;

public abstract class SQLSaveOperation<T extends SQLBackedDefinition> extends SQLOperation<T, Boolean, Void> {

	protected Entity<T> entity = null;
	
	public SQLSaveOperation(SQLBackedEntityManager<T> manager, Transaction transaction, Entity<T> entity) {
		super(manager, transaction);
		this.entity = entity;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Void> op() {		
		SQLConnection conn = this.manager.acquireConnection();
		
		boolean success = false;
		
		try {
			success = runQuery(conn);
			((SQLNode<T>) entity.getNode()).markCreated(success);
			entity.mark();
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}
	
	public abstract boolean runQuery(SQLConnection conn);

}
