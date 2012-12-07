package net.vurs.conn.hbase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Result;

import net.vurs.conn.ConnectionPool;
import net.vurs.conn.cache.CacheConnection;
import net.vurs.conn.hbase.operation.HBaseDeleteOperation;
import net.vurs.conn.hbase.operation.HBaseGetOperation;
import net.vurs.conn.hbase.operation.HBaseSaveOperation;
import net.vurs.conn.hbase.streamer.HBaseEntityStreamer;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;
import net.vurs.entity.Node;
import net.vurs.entity.Operation;
import net.vurs.entity.ProxyEntity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.service.definition.EntityService;
import net.vurs.transaction.Transaction;

public class HBaseBackedEntityManager<T extends HBaseBackedDefinition> extends Manager<T> {

	protected ConnectionPool<CacheConnection> cachePool = null;
	protected ConnectionPool<HBaseConnection> hbasePool = null;
	
	protected String tableName = null;
	
	protected HBaseEntityStreamer<T> entityStreamer = null;

	@SuppressWarnings("unchecked")
	public void setup(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys, PrimaryKey<?, ?> primaryKey) {
		super.setup(entityService, entityType, keys, primaryKey);

		this.cachePool = this.entityService.getConnectionService().getPool(
				CacheConnection.class, ConnectionPool.class);
		this.hbasePool = this.entityService.getConnectionService().getPool(
				HBaseConnection.class, ConnectionPool.class);
		
		this.tableName = entityType.getSimpleName();
	}
	
	public HBaseConnection acquireConnection() { return this.hbasePool.acquire(); }
	public String getTable() { return this.tableName; }
	public HBaseEntityStreamer<T> getStreamer() { return this.entityStreamer; }
	
	public Entity<T> wrapResult(String key, Result result) {
		Map<String, Object> parsed = this.entityStreamer.parse(result);
		return wrap(key, new Node<T>(this.def, this, parsed));
	}
	
	@Override
	public Entity<T> wrap(String key, Node<T> node) {
		return new Entity<T>(this.entityService, this.entityType, key, node);
	}
	
	@Override
	public Entity<T> getProxy(String key) {
		return new ProxyEntity<T>(this.entityService, this.entityType, key);
	}

	@Override
	public Entity<T> create(String key) {
		if (key == null) {
			key = this.entityStreamer.getPrimaryStreamer().generateKey();
		}
		
		return wrap(key, new Node<T>(this.def, this, new HashMap<String, Object>()));
	}

	@Override
	public Operation<Boolean, Map<String, Entity<T>>> get(List<String> keys, Transaction transaction) {
		HBaseGetOperation<T> op = new HBaseGetOperation<T>(this, transaction, keys);
		return op;
	}

	@Override
	public Operation<Boolean, Void> save(Entity<T> entity, Transaction transaction) {
		HBaseSaveOperation<T> op = new HBaseSaveOperation<T>(this, transaction, entity);
		return op;
	}

	@Override
	public Operation<Boolean, Void> delete(String key, Transaction transaction) {
		HBaseDeleteOperation<T> op = new HBaseDeleteOperation<T>(this, transaction, key);
		return op;
	}

}
