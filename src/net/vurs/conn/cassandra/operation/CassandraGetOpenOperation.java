package net.vurs.conn.cassandra.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;

import net.vurs.common.Triple;
import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;
import net.vurs.entity.OpenEntity;
import net.vurs.entity.operation.interfaces.PrefetchableOperation;
import net.vurs.transaction.Transaction;

public class CassandraGetOpenOperation<T extends CassandraBackedDefinition> extends CassandraOpenOperation<T, Boolean, Map<String, OpenEntity<T>>> implements PrefetchableOperation<T> {

	private List<String> keys = null;
	
	public CassandraGetOpenOperation(CassandraBackedOpenEntityManager<T> manager, Transaction transaction, List<String> keys) {
		super(manager, transaction);
		this.keys = keys;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Map<String, OpenEntity<T>>> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		Map<String, OpenEntity<T>> ret = new HashMap<String, OpenEntity<T>>();

		try {
			Map<String, List<ColumnOrSuperColumn>> data = 
				conn.get(keys, this.openManager.getColumnFamily(), 
											null, 
											this.openManager.getFullSlice(), 
											ConsistencyLevel.ONE);

			for (String key: keys) {
				List<ColumnOrSuperColumn> cols = data.get(key);
				if (cols.size() > 0) {
					ret.put(key, this.openManager.wrapOpenColumns(key, cols));
				}
			}
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Map<String, OpenEntity<T>>>(true, true, ret);
	}

	@Override
	public Collection<? extends Entity<T>> getEntities() {
		return this.getValue().values();
	}

	@Override
	public Manager<T> getManager() {
		return this.manager;
	}

}
