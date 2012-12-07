package net.vurs.service.definition;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.vurs.conn.Connection;
import net.vurs.entity.ComponentStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Manager;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.entity.backend.EntityBackend;
import net.vurs.transaction.Transaction;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;
import net.vurs.util.Reflection;

public class EntityService extends Service {
	
	private ConnectionService connectionService = null;
	private TransactionService transactionService = null;
	
	private ConcurrentHashMap<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> managers = null;
	private Map<Class<? extends Connection>, Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>>> componentStreamers = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.logger.info("Loading entity service");

		this.connectionService = serviceManager.getService(ConnectionService.class);
		this.transactionService = serviceManager.getService(TransactionService.class);
		
		this.managers = new ConcurrentHashMap<Class<? extends Definition<?>>, Manager<? extends Definition<?>>>();
		this.componentStreamers = new ConcurrentHashMap<Class<? extends Connection>, Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>>>();
		
		setupBackends();
	}
	
	@SuppressWarnings("unchecked")
	private void setupBackends() {
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(EntityBackend.class));

		while (itr.hasNext()) {
			Class<? extends EntityBackend<?>> cls = (Class<? extends EntityBackend<?>>) itr.next();
			if (cls.equals(EntityBackend.class)) continue;
			
			this.logger.info(String.format("Found entity backend: %s", cls.getName()));
						
			try {
				Class<?> paramType = Reflection.getParamType(cls, 0);
				Class<? extends Connection> connType = (Class<? extends Connection>) paramType;
				Constructor<? extends EntityBackend<?>> ctor = cls.getConstructor(EntityService.class);
				
				EntityBackend<?> backend = ctor.newInstance(this);
				
				this.componentStreamers.put(connType, backend.loadStreamers());
				this.managers.putAll(backend.loadManagers());
				
				backend.finalizeInitialization();
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}

	public Transaction getGlobalTransaction() { 
		return this.transactionService.getGlobalTransaction(); 
	}
	
	public Transaction startGlobalTransaction() { 
		return this.transactionService.startGlobalTransaction(); 
	}
	
	public ConnectionService getConnectionService() {
		return this.connectionService;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Definition<? extends Connection>> Manager<E> getManager(Class<E> type) {
		return (Manager<E>) this.managers.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Definition<? extends Connection>, M extends Manager<E>> M getManager(Class<E> type, Class<M> manager) {
		return (M) this.managers.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public <C, I, O, S extends ComponentStreamer<C, I, O>> Class<S> getStreamer(Class<? extends Connection> connType, Class<S> streamerClass, Class<O> output) {
		return (Class<S>) this.componentStreamers.get(connType).get(output);
	}
	
}
