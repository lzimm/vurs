package net.vurs.conn.cassandra.operation;

import org.apache.cassandra.thrift.ConsistencyLevel;

import net.vurs.common.Triple;
import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.transaction.Transaction;

public class CassandraSaveOperation<T extends CassandraBackedDefinition> extends CassandraOperation<T, Boolean, Void> {

	private Entity<T> entity = null;
	
	public CassandraSaveOperation(CassandraBackedEntityManager<T> manager, Transaction transaction, Entity<T> entity) {
		super(manager, transaction);
		this.entity = entity;
	}

	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			String key = this.entity.getKey();
			conn.insert(key, this.manager.getColumnFamily(), 
							this.manager.getStreamer().stream(entity), 
							ConsistencyLevel.QUORUM);
			
			this.entity.mark();
			
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}
	
}
