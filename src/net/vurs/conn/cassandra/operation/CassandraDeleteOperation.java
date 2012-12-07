package net.vurs.conn.cassandra.operation;

import org.apache.cassandra.thrift.ConsistencyLevel;

import net.vurs.common.Triple;
import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.transaction.Transaction;

public class CassandraDeleteOperation<T extends CassandraBackedDefinition> extends CassandraOperation<T, Boolean, Void> {

	private String key = null;
	
	public CassandraDeleteOperation(CassandraBackedEntityManager<T> manager, Transaction transaction, String key) {
		super(manager, transaction);
		this.key = key;
	}

	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			conn.remove(this.key, this.manager.getColumnFamily(), ConsistencyLevel.QUORUM);
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}
	
}
