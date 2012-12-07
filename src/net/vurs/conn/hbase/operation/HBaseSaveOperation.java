package net.vurs.conn.hbase.operation;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;

import net.vurs.common.Triple;
import net.vurs.conn.hbase.HBaseBackedEntityManager;
import net.vurs.conn.hbase.HBaseConnection;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.transaction.Transaction;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class HBaseSaveOperation<T extends HBaseBackedDefinition> extends HBaseOperation<T, Boolean, Void> {

	private Entity<T> entity = null;
	
	public HBaseSaveOperation(HBaseBackedEntityManager<T> manager, Transaction transaction, Entity<T> entity) {
		super(manager, transaction);
		this.entity = entity;
	}

	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		HBaseConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			Put put = new Put(Serialization.serialize(this.entity.getKey()));
			for (KeyValue kv: this.manager.getStreamer().stream(entity)) {
				put.add(kv);
			}
			
			conn.put(this.manager.getTable(), put);
			
			this.entity.mark();
			
			success = true;
		} catch (Exception e) {
			ErrorControl.logException(e);
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}
	
}
