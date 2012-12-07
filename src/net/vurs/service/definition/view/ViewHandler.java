package net.vurs.service.definition.view;

import net.vurs.common.Pair;
import net.vurs.service.definition.GeoService;
import net.vurs.service.definition.LogicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip.Location;

public class ViewHandler<T extends ViewType> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected LogicService logicService = null;
	protected GeoService geoService = null;
	
	public void setup(LogicService logicService) {
		this.logicService = logicService;
		this.geoService = logicService.getGeoService();
		
		this.init();
	}
	
	public void init() {}
	
	public Pair<Double, Double> getLocation(ViewRequest<T> req) {
		Location location = this.geoService.lookupIP(req.getIP());
		if (location == null) {
			return GeoService.DEFAULT_LAT_LON;
		} else {
			Double lat = Double.valueOf(Float.valueOf(location.latitude).toString());
			Double lon = Double.valueOf(Float.valueOf(location.longitude).toString());
			return new Pair<Double, Double>(lat, lon);
		}
		
	}
	
}
