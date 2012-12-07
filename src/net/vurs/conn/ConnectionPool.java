package net.vurs.conn;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.vurs.common.ConcurrentLinkedQueueMap;
import net.vurs.util.ErrorControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPool<T extends Connection> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ConcurrentLinkedQueueMap<String, T> connections = null;
	private ConcurrentLinkedQueueMap<String, T> queue = null;
	
	private Class<T> poolType = null;
	private Constructor<T> connConstructor = null;
	
	public ConnectionPool() {
		this.connections = new ConcurrentLinkedQueueMap<String, T>();
		this.queue = new ConcurrentLinkedQueueMap<String, T>();
	}
	
	@SuppressWarnings("unchecked")
	public void bindType(Class<?> poolType) {
		logger.info(String.format("Binding client pool to %s", poolType.getName()));
		this.poolType = (Class<T>) poolType;
		
		try {
			this.connConstructor = this.poolType.getConstructor(String.class);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public T acquire() { return this.acquire(null); }
	public T acquire(String params) {
		String poolKey = getPoolKey(params);
		
		T connection = queue.get(poolKey).poll();
		
		if (connection == null) {
			connection = create(poolKey);
		}
		
		return connection;
	}
	
	@SuppressWarnings("unchecked")
	public void release(Connection client) {
		queue.get(client.getPoolKey()).add((T) client);
	}
	
	private T create(String poolKey) {
		T instance = null;
		
		try {
			instance = this.connConstructor.newInstance(poolKey);
			instance.hookPool(this);
			this.connections.get(poolKey).add(instance);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
		
		return instance;
	}

	public void shutDown() {
		for (ConcurrentLinkedQueue<T> q: this.connections.values()) {
			for (T conn: q) {
				conn.close();
			}
		}
	}
	
	protected String getPoolKey(String params) {
		if (params == null) {
			return getClass().getName();
		} else {
			return params;
		}
	}

}
