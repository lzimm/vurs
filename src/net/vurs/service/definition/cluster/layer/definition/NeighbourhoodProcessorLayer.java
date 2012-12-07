package net.vurs.service.definition.cluster.layer.definition;

import net.vurs.service.ServiceManager;
import net.vurs.service.definition.NeighbourhoodService;
import net.vurs.service.definition.cluster.layer.ClusterLayer;

public class NeighbourhoodProcessorLayer extends ClusterLayer {

	@Override
	public void activate(ServiceManager serviceManager) {
		serviceManager.getService(NeighbourhoodService.class).startProcessor();
	}

	@Override
	public void deactivate(ServiceManager serviceManager) {
		serviceManager.getService(NeighbourhoodService.class).stopProcessor();
	}

}
