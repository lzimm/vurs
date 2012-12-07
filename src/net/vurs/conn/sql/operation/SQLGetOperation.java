package net.vurs.conn.sql.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vurs.common.Triple;
import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.SQLNode;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;
import net.vurs.entity.operation.interfaces.PrefetchableOperation;
import net.vurs.transaction.Transaction;

public class SQLGetOperation<T extends SQLBackedDefinition> extends SQLOperation<T, Boolean, Map<String, Entity<T>>> implements PrefetchableOperation<T> {

	private List<String> keys = null;
	
	public SQLGetOperation(SQLBackedEntityManager<T> manager, Transaction transaction, List<String> keys) {
		super(manager, transaction);
		this.keys = keys;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Map<String, Entity<T>>> op() {
		SQLConnection conn = this.manager.acquireConnection();
		
		Map<String, Entity<T>> ret = new HashMap<String, Entity<T>>();

		try {
			if (this.keys.size() > 0) {
				StringBuilder values = new StringBuilder();
				
				for (String key: keys) {
					values.append('\'').append(key).append("',");
				}
				
				String valuePart = values.substring(0, values.length() - 1);
				
				StringBuilder query = new StringBuilder(this.manager.getStart())
												.append(valuePart)
												.append(')');
				
				List<Map<String, Object>> results = conn.query(query.toString());
				for (Map<String, Object> r: results) {
					String key = this.manager.getStreamer().getPrimaryStreamer().asString(r.get(this.manager.getPrimaryKey().getName()));
					Map<String, Object> parsed = this.manager.getStreamer().parse(r);
					ret.put(key, this.manager.wrap(key, new SQLNode<T>(manager.getDefinition(), this.manager, parsed, true)));
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
