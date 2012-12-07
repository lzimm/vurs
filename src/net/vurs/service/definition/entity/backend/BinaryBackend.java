package net.vurs.service.definition.entity.backend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.binary.BinaryConnection;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.entity.ComponentStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Manager;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.Reflection;

public class BinaryBackend extends EntityBackend<BinaryConnection> {    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
	private Map<Class<?>, Class<? extends BinaryComponentStreamer<?>>> componentStreamers = null;
	
	public BinaryBackend(EntityService entityService) {
		this.logger.info("Loading binary entity backend");
	}
	
	@SuppressWarnings("unchecked")
	private void setupStreamers() {
		this.componentStreamers = new HashMap<Class<?>, Class<? extends BinaryComponentStreamer<?>>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(BinaryComponentStreamer.class));
		
		while (itr.hasNext()) {
			Class<? extends BinaryComponentStreamer<?>> cls = (Class<? extends BinaryComponentStreamer<?>>) itr.next();
		    Class<?> paramType = Reflection.getParamType(cls, 0);
		    
		    this.logger.info(String.format("Found component streamer: %s", cls.getName()));
		    this.componentStreamers.put(paramType, cls);
		}
	}

	@Override
	public Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> loadStreamers() {
		setupStreamers();
		
		Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> ret = new HashMap<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>>();
		for (Entry<Class<?>, Class<? extends BinaryComponentStreamer<?>>> e: this.componentStreamers.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}
	
	@Override
	public Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> loadManagers() {
		return new HashMap<Class<? extends Definition<?>>, Manager<? extends Definition<?>>>();
	}
	
	@Override
	public void finalizeInitialization() {
	}

}
