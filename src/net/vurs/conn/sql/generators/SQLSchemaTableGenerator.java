package net.vurs.conn.sql.generators;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;

public abstract class SQLSchemaTableGenerator<T extends SQLBackedDefinition> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	protected Map<Class<?>, Class<? extends SQLComponentStreamer<?>>> streamers = null;
	
	protected void setup(EntityService entityService, Map<Class<?>, Class<? extends SQLComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.streamers = streamers;
	}
	
	protected SQLComponentStreamer<?> getStreamer(Class<?> type, ParameterizedType paramType) {
		try {
			Class<? extends SQLComponentStreamer<?>> cls = (Class<? extends SQLComponentStreamer<?>>) this.streamers.get(type);
			Constructor<? extends SQLComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			SQLComponentStreamer<?> instance = ctor.newInstance(this.entityService, type, paramType);
			return instance;
		} catch (Exception e) {
			ErrorControl.fatal(String.format("Trying to use illegal type as column index: %s", type.getSimpleName()));
			return null;
		}
	}
	
	protected abstract void generate(Class<? extends SQLBackedDefinition> definition, PrintStream out);
	
}
