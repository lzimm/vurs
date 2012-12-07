package net.vurs.conn.hbase.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import net.vurs.common.Triple;
import net.vurs.conn.hbase.HBaseBackedEntityManager;
import net.vurs.conn.hbase.HBaseConnection;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;
import net.vurs.entity.operation.interfaces.PrefetchableOperation;
import net.vurs.transaction.Transaction;
import net.vurs.util.Serialization;

public class HBaseGetOperation<T extends HBaseBackedDefinition> extends HBaseOperation<T, Boolean, Map<String, Entity<T>>> implements PrefetchableOperation<T> {

	private List<String> keys = null;
	
	public HBaseGetOperation(HBaseBackedEntityManager<T> manager, Transaction transaction, List<String> keys) {
		super(manager, transaction);
		this.keys = keys;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Map<String, Entity<T>>> op() {
		HBaseConnection conn = this.manager.acquireConnection();
		
		Map<String, Entity<T>> ret = new HashMap<String, Entity<T>>();
		
		try {
			for (String key: keys) {
				Get getOp = new Get(Serialization.serialize(key));
				Result data = conn.get(this.manager.getTable(), getOp);
				if (!data.isEmpty()) {
					ret.put(key, this.manager.wrapResult(key, data));
				}
			}
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Map<String, Entity<T>>>(true, true, ret);
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
