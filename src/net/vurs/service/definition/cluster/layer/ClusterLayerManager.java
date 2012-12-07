package net.vurs.service.definition.cluster.layer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterLayerManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Map<Class<? extends ClusterLayer>, ClusterLayer> layerByClass = null;
	private Map<Class<? extends ClusterLayer>, Integer> ordinalByClass = null;
	private Map<Integer, Class<? extends ClusterLayer>> classByOrdinal = null;
	private Map<Integer, ClusterLayer> layerByOrdinal = null;
	
	public ClusterLayerManager() {
		this.logger.info("Loading cluster layers");
		
		this.layerByClass = new HashMap<Class<? extends ClusterLayer>, ClusterLayer>();
		this.ordinalByClass = new HashMap<Class<? extends ClusterLayer>, Integer>();
		this.classByOrdinal = new HashMap<Integer, Class<? extends ClusterLayer>>();
		this.layerByOrdinal = new HashMap<Integer, ClusterLayer>();
		
		this.setupLayers();
	}

	@SuppressWarnings("unchecked")
	private void setupLayers() {
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(ClusterLayer.class));
		
		while (itr.hasNext()) {
			Class<? extends ClusterLayer> cls = (Class<? extends ClusterLayer>) itr.next();
			
			this.logger.info(String.format("Found cluster layer: %s", cls.getName()));
			
			try {
				ClusterLayer instance = cls.newInstance();
				this.layerByClass.put(cls, instance);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
		
		Class<? extends ClusterLayer>[] orderedLayers = (Class<? extends ClusterLayer>[]) this.layerByClass.keySet().toArray(new Class<?>[this.layerByClass.size()]);
		Arrays.sort(orderedLayers, new Comparator<Class<? extends ClusterLayer>>() {

			@Override
			public int compare(Class<? extends ClusterLayer> arg0, Class<? extends ClusterLayer> arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
			
		});
		
		for (int i = 0; i < orderedLayers.length; i++) {
			Class<? extends ClusterLayer> layer = orderedLayers[i];
			this.ordinalByClass.put(layer, i);
			this.classByOrdinal.put(i, layer);
			this.layerByOrdinal.put(i, this.layerByClass.get(layer));
		}
	}
	
	public Integer getOrdinal(Class<? extends ClusterLayer> layer) { return this.ordinalByClass.get(layer); }
	
	public Class<? extends ClusterLayer> getLayer(int ordinal) { return this.classByOrdinal.get(ordinal); }
	public ClusterLayer getLayer(Class<? extends ClusterLayer> cls) { return this.layerByClass.get(cls); }
	
}
