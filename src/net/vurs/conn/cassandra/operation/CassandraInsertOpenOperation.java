package net.vurs.conn.cassandra.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SuperColumn;

import net.vurs.common.TimeUUID;
import net.vurs.common.Triple;
import net.vurs.conn.Connection;
import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Definition;
import net.vurs.transaction.Transaction;
import net.vurs.util.Serialization;

public class CassandraInsertOpenOperation<T extends CassandraBackedDefinition, E extends Definition<? extends Connection>> extends CassandraOpenOperation<T, Boolean, Void> {
	
	private String key = null;
	private String superColumn = null;
	private List<byte[]> additions = null;


	public CassandraInsertOpenOperation(CassandraBackedOpenEntityManager<T> manager, Transaction transaction, String key, String superColumn, List<byte[]> additions) {
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
			List<ColumnOrSuperColumn> cols = new ArrayList<ColumnOrSuperColumn>(this.additions.size());
			
			Long ms = System.currentTimeMillis();
			for (byte[] addition: this.additions) {
				ColumnOrSuperColumn csc = new ColumnOrSuperColumn();
				csc.super_column = new SuperColumn();
				csc.super_column.name = Serialization.serialize(this.superColumn);
				csc.super_column.addToColumns(new Column(new TimeUUID().getBytes(), addition, ms));
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
