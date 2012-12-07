package net.vurs.service.definition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.vurs.conn.Connection;
import net.vurs.conn.ConnectionPool;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

public class ConnectionService extends Service {

	private HashMap<Class<? extends Connection>, ConnectionPool<? extends Connection>> poolMap = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.logger.info("Loading connection service");
		
		setupPools();
		
		this.logger.info("Registering shutdown hook for connection service");
		registerShutdownHook(this.poolMap.values());
	}
	
	@SuppressWarnings("unchecked")
	private void setupPools() {
		this.logger.info("Setting up connection pools");
		
		this.poolMap = new HashMap<Class<? extends Connection>, ConnectionPool<? extends Connection>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(Connection.class));
		
		while (itr.hasNext()) {
			Class<? extends Connection> cls = (Class<? extends Connection>) itr.next();
			
			if (cls.equals(Connection.class)) continue;
			
			this.logger.info(String.format("Found connection class %s, looking for pool implementation", cls.getName()));
			
			try {
				Class<? extends ConnectionPool<? extends Connection>> pCls = null;
				
				Iterator<Class<?>> pItr = new ClassWalker(ClassWalkerFilter.extendingWithParam(ConnectionPool.class, cls));
				
				if (pItr.hasNext()) {
					this.logger.info(String.format("Found custom connection pool for %s", cls.getName()));
					pCls = (Class<? extends ConnectionPool<? extends Connection>>) pItr.next();
				} else {
					this.logger.info(String.format("Using generic connection pool for %s", cls.getName()));
					pCls = (Class<? extends ConnectionPool<? extends Connection>>) ConnectionPool.class;
				}
				
				ConnectionPool<? extends Connection> pool = pCls.newInstance();
				pool.bindType(cls);
				
				this.poolMap.put(cls, pool);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Connection> ConnectionPool<T> getPool(Class<T> type) {
		return (ConnectionPool<T>) this.poolMap.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Connection, P extends ConnectionPool<T>> P getPool(Class<T> type, Class<P> poolType) {
		return (P) this.poolMap.get(type);
	}
	
	private void registerShutdownHook(final Collection<ConnectionPool<? extends Connection>> pools) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	ConnectionService.this.logger.info("Shutting down connection pools");
            	for (ConnectionPool<? extends Connection> pool: pools) {
            		pool.shutDown();
            	}
            }
        });	
	}
	
}
