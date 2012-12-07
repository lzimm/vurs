package net.vurs.service.definition.cluster.layer.definition;

import net.vurs.service.ServiceManager;
import net.vurs.service.definition.ConceptService;
import net.vurs.service.definition.cluster.layer.ClusterLayer;

public class ConceptFarmerLayer extends ClusterLayer {

	@Override
	public void activate(ServiceManager serviceManager) {
		serviceManager.getService(ConceptService.class).startFarmer();
	}

	@Override
	public void deactivate(ServiceManager serviceManager) {	
		serviceManager.getService(ConceptService.class).stopFarmer();
	}

}
