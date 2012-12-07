package net.vurs.conn.cassandra.operation;

import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Operation;
import net.vurs.transaction.Transaction;

public abstract class CassandraOperation<T extends CassandraBackedDefinition, R, V> extends Operation<R, V> {

	protected CassandraBackedEntityManager<T> manager = null;
	
	public CassandraOperation(CassandraBackedEntityManager<T> manager,
			Transaction transaction) {
		super(transaction);
		this.manager = manager;
	}

}
