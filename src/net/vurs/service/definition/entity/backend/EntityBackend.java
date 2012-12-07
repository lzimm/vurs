package net.vurs.service.definition.entity.backend;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.Connection;
import net.vurs.entity.ComponentStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Manager;

public abstract class EntityBackend<T extends Connection> {
    protected static final String ENTITY_PACKAGE = "net.vurs.entity.definition";
    protected static final String MANAGER_PACKAGE = "net.vurs.service.definition.entity.manager";
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
	public abstract Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> loadStreamers();
	public abstract Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> loadManagers();
	public abstract void finalizeInitialization();
	
}
