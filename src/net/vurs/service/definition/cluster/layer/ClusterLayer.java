package net.vurs.service.definition.cluster.layer;

import net.vurs.service.ServiceManager;

public abstract class ClusterLayer {
	
	public abstract void activate(ServiceManager serviceManager);
	public abstract void deactivate(ServiceManager serviceManager);

}
