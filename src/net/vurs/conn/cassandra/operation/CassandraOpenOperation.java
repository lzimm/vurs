package net.vurs.conn.cassandra.operation;

import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.transaction.Transaction;

public abstract class CassandraOpenOperation<T extends CassandraBackedDefinition, R, V> extends CassandraOperation<T, R, V> {
	
	protected CassandraBackedOpenEntityManager<T> openManager = null;

	public CassandraOpenOperation(CassandraBackedOpenEntityManager<T> manager,
			Transaction transaction) {
		super(manager, transaction);
		this.openManager = manager;
	}

}
