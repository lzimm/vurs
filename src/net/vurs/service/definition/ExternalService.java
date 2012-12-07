package net.vurs.service.definition;

import net.vurs.service.Service;
import net.vurs.service.ServiceManager;

import com.freebase.api.Freebase;

public class ExternalService extends Service {

	private Freebase freebase = null;

	@Override
	public void init(ServiceManager serviceManager) {
		this.freebase = Freebase.getFreebase();
	}
	
	public Freebase getFreebase() { return this.freebase; }
	
}
