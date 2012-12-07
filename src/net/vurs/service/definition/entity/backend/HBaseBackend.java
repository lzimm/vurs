package net.vurs.service.definition.entity.backend;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.hbase.HBaseBackedEntityManager;
import net.vurs.conn.hbase.HBaseConnection;
import net.vurs.conn.hbase.generators.HBaseSchemaGenerator;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.ComponentStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Manager;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;
import net.vurs.util.Reflection;
import net.vurs.util.RelativePath;

public class HBaseBackend extends EntityBackend<HBaseConnection> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private EntityService entityService;
    
	private Map<Class<? extends HBaseBackedDefinition>, Manager<? extends HBaseBackedDefinition>> managers = null;
	private Map<Class<?>, Class<? extends HBaseComponentStreamer<?>>> componentStreamers = null;
	
	public HBaseBackend(EntityService entityService) {
		this.logger.info("Loading hbase entity backend");
		
		this.entityService = entityService;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Class<? extends HBaseBackedDefinition>, Class<? extends HBaseBackedEntityManager<?>>> findManagers() {
		this.logger.info("Finding entity managers");
		Map<Class<? extends HBaseBackedDefinition>, Class<? extends HBaseBackedEntityManager<?>>> managers = new HashMap<Class<? extends HBaseBackedDefinition>, Class<? extends HBaseBackedEntityManager<?>>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(HBaseBackedEntityManager.class));
		
		while (itr.hasNext()) {
			Class<? extends HBaseBackedEntityManager<?>> cls = (Class<? extends HBaseBackedEntityManager<?>>) itr.next();
			
			this.logger.info(String.format("Found entity manager: %s", cls.getName()));
			
		    Class<?> paramType = Reflection.getParamType(cls, 0);
	    	Class<? extends HBaseBackedDefinition> definition = (Class<? extends HBaseBackedDefinition>) paramType;
	    	managers.put(definition, cls);
		}
		
		return managers;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupEntities() {
		this.managers = new HashMap<Class<? extends HBaseBackedDefinition>, Manager<? extends HBaseBackedDefinition>>();
		
		Map<Class<? extends HBaseBackedDefinition>, Class<? extends HBaseBackedEntityManager<?>>> managers = findManagers();
		Map<Class<? extends HBaseBackedDefinition>, List<FieldKey<?>>> keysByClass = new HashMap<Class<? extends HBaseBackedDefinition>, List<FieldKey<?>>>();
		Map<Class<? extends HBaseBackedDefinition>, PrimaryKey<?, ?>> primaryKeys = new HashMap<Class<? extends HBaseBackedDefinition>, PrimaryKey<?, ?>>();
		
		this.logger.info("Setting up entities");
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.isConcreteClass(),
												ClassWalkerFilter.extending(HBaseBackedDefinition.class));
		
		while (itr.hasNext()) {
			Class<? extends HBaseBackedDefinition> cls = (Class<? extends HBaseBackedDefinition>) itr.next();
			
			this.logger.info(String.format("Setting up entity: %s", cls.getName()));
			
			List<FieldKey<?>> keys = new ArrayList<FieldKey<?>>();
			PrimaryKey<?, ?> primaryKey = null;
			
			for (Field field: cls.getDeclaredFields()) {
				try {
					if (field.getType().equals(FieldKey.class)) {
					    Class<?> paramType = Reflection.getParamType(field, 0);
					    ParameterizedType paramSubType = Reflection.getParamSubType(field, 0);
					    FieldKey<?> key = new FieldKey(cls, paramType, paramSubType, field.getName(), field.getAnnotations());
						field.set(null, key);
						keys.add(key);
					} else if (field.getType().equals(PrimaryKey.class)) {
					    Class<?> paramType = Reflection.getParamType(field, 0);
					    ParameterizedType paramSubType = Reflection.getParamSubType(field, 0);
					    primaryKey = new PrimaryKey(cls, paramType, paramSubType, field.getName());
						field.set(null, primaryKey);						
					}
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
			
			keysByClass.put(cls, keys);
			primaryKeys.put(cls, primaryKey);
			
			HBaseBackedEntityManager em = null;
			Class<? extends HBaseBackedEntityManager<?>> manager = managers.get(cls);
			if (manager != null) {
				try {
					em = manager.newInstance();
					this.logger.info(String.format("Using manager: %s for: %s", manager.getName(), cls.getName()));
				} catch (Exception e) {
					ErrorControl.logException(e);
					em = null;
				}
			}
			
			if (em == null) {
				em = new HBaseBackedEntityManager();
				this.logger.info(String.format("Using generic manager for: %s", cls.getName()));
			}
			
			this.managers.put(cls, em);
		}
		
		for (Entry<Class<? extends HBaseBackedDefinition>, Manager<? extends HBaseBackedDefinition>> entry: this.managers.entrySet()) {
			Class<? extends Definition> cls = entry.getKey();
			Manager em = entry.getValue();
			List<FieldKey<?>> keys = keysByClass.get(cls);
			PrimaryKey<?, ?> primaryKey = primaryKeys.get(cls);
			em.setup(this.entityService, cls, keys, primaryKey);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setupStreamers() {
		this.componentStreamers = new HashMap<Class<?>, Class<? extends HBaseComponentStreamer<?>>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(HBaseComponentStreamer.class));
		
		while (itr.hasNext()) {
			Class<? extends HBaseComponentStreamer<?>> cls = (Class<? extends HBaseComponentStreamer<?>>) itr.next();
		    Class<?> paramType = Reflection.getParamType(cls, 0);
		    
		    this.logger.info(String.format("Found component streamer: %s", cls.getName()));
		    this.componentStreamers.put(paramType, cls);
		}
	}
	
	private void generateSchema() {
		File schemaFile = new File(RelativePath.root().getAbsolutePath()
								.concat(File.separator)
								.concat("hbase-conf.xml"));
		
		HBaseSchemaGenerator generator = new HBaseSchemaGenerator(this.entityService, 
													this.managers.keySet(), 
													this.componentStreamers);

		generator.generate(schemaFile);
	}

	@Override
	public Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> loadStreamers() {
		setupStreamers();
		
		Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> ret = new HashMap<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>>();
		for (Entry<Class<?>, Class<? extends HBaseComponentStreamer<?>>> e: this.componentStreamers.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}
	
	@Override
	public Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> loadManagers() {
		setupEntities();
		
		Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> ret = new HashMap<Class<? extends Definition<?>>, Manager<? extends Definition<?>>>();
		for (Entry<Class<? extends HBaseBackedDefinition>, Manager<? extends HBaseBackedDefinition>> e: this.managers.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}
	
	@Override
	public void finalizeInitialization() {
		generateSchema();
	}

}
