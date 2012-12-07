package net.vurs.service.definition;

import java.util.HashMap;
import java.util.Iterator;

import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.logic.LogicController;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

public class LogicService extends Service {
	
	private EntityService entityService = null;
	private ClusterService clusterService = null;
	private NLPService nlpService = null;
	private ExternalService externalService = null;
	private GeoService geoService = null;
	private ConceptService conceptService = null;
	private NeighbourhoodService neighbourhoodService = null;

	private HashMap<Class<? extends LogicController>, LogicController> controllers;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.logger.info("Loading logic service");
		
		this.entityService = serviceManager.getService(EntityService.class);
		this.clusterService = serviceManager.getService(ClusterService.class);
		this.nlpService = serviceManager.getService(NLPService.class);
		this.externalService = serviceManager.getService(ExternalService.class);
		this.geoService = serviceManager.getService(GeoService.class);
		this.conceptService = serviceManager.getService(ConceptService.class);
		this.neighbourhoodService = serviceManager.getService(NeighbourhoodService.class);
		
		setupControllers();
	}

	@SuppressWarnings("unchecked")
	private void setupControllers() {
		this.logger.info("Setting up controllers");
		
		this.controllers = new HashMap<Class<? extends LogicController>, LogicController>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(LogicController.class));
		
		while (itr.hasNext()) {
			Class<? extends LogicController> cls = (Class<? extends LogicController>) itr.next();
			
			try {
				LogicController controller = cls.newInstance();
				this.controllers.put(cls, controller);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
		
		for (LogicController controller: this.controllers.values()) {
			controller.setup(this.entityService, this.clusterService, this.nlpService, this.externalService, this.geoService, this.conceptService, this.neighbourhoodService, this);
			controller.init();
		}
	}
	
	public EntityService getEntityService() { return this.entityService; }
	public ClusterService getClusterService() { return this.clusterService; }
	public GeoService getGeoService() { return this.geoService; }
	
	@SuppressWarnings("unchecked")
	public <T extends LogicController> T get(Class<T> cls) {
		return (T) this.controllers.get(cls);
	}

}
