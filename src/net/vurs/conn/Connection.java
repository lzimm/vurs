package net.vurs.conn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Connection {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ConnectionPool<? extends Connection> pool = null;
	protected String poolKey = null;
	
	public Connection(String poolKey) {
		this.poolKey = poolKey;
	}
	
	protected void hookPool(ConnectionPool<? extends Connection> pool) {
		this.logger.info("Binding new client to pool");
		this.pool = pool;
	}
	
	public void release() {
		this.pool.release(this);
	}
	
	public String getPoolKey() { return this.poolKey; }
	
	protected abstract void close();
	
}
