package net.vurs.service.definition;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import net.vurs.service.EventDispatcher;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.cluster.ClusterHeartbeat;
import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.service.definition.cluster.layer.ClusterLayer;
import net.vurs.service.definition.cluster.layer.ClusterLayerManager;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

public class ClusterService extends Service {
	protected static final Integer START_PORT = 4444;
	
	private ServiceManager serviceManager = null;
	
	private ClusterLayerManager layerManager = null;
	private ClusterHeartbeat heartbeat = null;	
	private ConcurrentHashMap<Class<? extends ClusterProtocol>, ClusterProtocol> protocols = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		
		this.layerManager = new ClusterLayerManager();
		this.heartbeat = new ClusterHeartbeat(this.eventDispatcher, this.layerManager);
		this.protocols = new ConcurrentHashMap<Class<? extends ClusterProtocol>, ClusterProtocol>();
		
		setupProtocols();
	}
	
	@Override
	public void postInit() {
		for (ClusterProtocol protocol: this.protocols.values()) {
			protocol.init(this.serviceManager);
		}
	}

	@SuppressWarnings("unchecked")
	private void setupProtocols() {
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(ClusterProtocol.class));

		Integer port = START_PORT;
		while (itr.hasNext()) {
			Class<? extends ClusterProtocol> cls = (Class<? extends ClusterProtocol>) itr.next();
			if (cls.equals(ClusterProtocol.class)) continue;
			
			this.logger.info(String.format("Found cluster protocol: %s, binding to port %s", cls.getName(), port));
					
			try {
				Constructor<? extends ClusterProtocol> ctor = cls.getConstructor(EventDispatcher.class, ClusterService.class, Integer.class);
				ClusterProtocol protocol = ctor.newInstance(this.eventDispatcher, this, port++);
				this.protocols.put(cls, protocol);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}

	public ClusterHeartbeat getHeartbeat() {
		return this.heartbeat;
	}
	
	@SuppressWarnings("unchecked")
	public <P extends ClusterProtocol> P getProtocol(Class<P> protocol) {
		return (P) this.protocols.get(protocol);
	}
	
	public boolean onActiveLayer(Class<? extends ClusterLayer> layer) {
		return this.heartbeat.isActive(layer);
	}

	public ClusterLayerManager getLayerManager() { 
		return this.layerManager;
	}
	
}
