package net.vurs.service.definition;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.vurs.common.Pair;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@SuppressWarnings("unused")
public class GeoService extends Service {
	public static final Pair<Double, Double> DEFAULT_LAT_LON = new Pair<Double, Double>(100.0D, 100.0D);
	
	private ExternalService externalService = null;
	private LookupService lookupService = null;
	
	@Override
    public void init(ServiceManager serviceManager) {
		this.externalService = serviceManager.getService(ExternalService.class);
		
    	try {
    		File dataFile = new File(RelativePath.root().getAbsolutePath()
    				.concat(File.separator)
    				.concat("db")
    				.concat(File.separator)
    				.concat("geoip")
    				.concat(File.separator)
    				.concat("GeoIPCity.dat"));
    		
    		this.lookupService = new LookupService(dataFile.getPath(), LookupService.GEOIP_MEMORY_CACHE);
    	} catch (Exception e) {
    		ErrorControl.logException(e);
    	}
    }
    
    public Location lookupIP(String ip) {
    	return this.lookupService.getLocation(ip);
    }
	
}
