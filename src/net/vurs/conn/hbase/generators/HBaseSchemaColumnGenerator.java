package net.vurs.conn.hbase.generators;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;

public abstract class HBaseSchemaColumnGenerator<T extends HBaseBackedDefinition> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	protected Map<Class<?>, Class<? extends HBaseComponentStreamer<?>>> streamers = null;
	
	protected void setup(EntityService entityService, Map<Class<?>, Class<? extends HBaseComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.streamers = streamers;
	}
	
	@SuppressWarnings("unchecked")
	protected HBaseColumnIndexStreamer<?> getStreamer(Class<?> type, ParameterizedType paramType) {
		try {
			Class<? extends HBaseColumnIndexStreamer<?>> cls = (Class<? extends HBaseColumnIndexStreamer<?>>) this.streamers.get(type);
			Constructor<? extends HBaseColumnIndexStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			HBaseColumnIndexStreamer<?> instance = ctor.newInstance(this.entityService, type, paramType);
			return instance;
		} catch (Exception e) {
			ErrorControl.fatal(String.format("Trying to use illegal type as column index: %s", type.getSimpleName()));
			return null;
		}
	}
	
	protected abstract void generate(Class<? extends HBaseBackedDefinition> def, ParameterizedType definitionType, Document xmldoc, Element column);
	
}
