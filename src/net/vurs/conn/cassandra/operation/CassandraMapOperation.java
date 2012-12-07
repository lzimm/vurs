package net.vurs.conn.cassandra.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SuperColumn;

import net.vurs.common.Triple;
import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.transaction.Transaction;
import net.vurs.util.Serialization;

public class CassandraMapOperation<T extends CassandraBackedDefinition> extends CassandraOpenOperation<T, Boolean, Void> {
	
	private String key = null;
	private String superColumn = null;
	private Map<String, byte[]> additions = null;

	public CassandraMapOperation(CassandraBackedOpenEntityManager<T> manager, Transaction transaction, String key, String superColumn, Map<String, byte[]> additions) {
		super(manager, transaction);
		this.key = key;
		this.superColumn = superColumn;
		this.additions = additions;
	}

	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			List<ColumnOrSuperColumn> cols = new ArrayList<ColumnOrSuperColumn>(additions.size());
			
			Long ms = System.currentTimeMillis();
			for (Entry<String, byte[]> entry: additions.entrySet()) {
				ColumnOrSuperColumn csc = new ColumnOrSuperColumn();
				csc.super_column = new SuperColumn();
				csc.super_column.name = Serialization.serialize(superColumn);
				csc.super_column.addToColumns(new Column(Serialization.serialize(entry.getKey()), entry.getValue(), ms));
				cols.add(csc);
			}
			
			conn.insert(key, this.openManager.getColumnFamily(), cols, ConsistencyLevel.QUORUM);
			
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}

}
