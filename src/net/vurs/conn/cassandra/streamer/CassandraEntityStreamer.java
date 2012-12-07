package net.vurs.conn.cassandra.streamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.Key;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class CassandraEntityStreamer<T extends CassandraBackedDefinition> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	
	protected Class<T> entityType = null;
	protected List<FieldKey<?>> keys = null;
	
	protected Map<String, CassandraComponentStreamer<?>> streamers = null;
	protected CassandraPrimaryKeyStreamer<?> primaryStreamer = null;
	
	public CassandraEntityStreamer(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys, PrimaryKey<?, ?> primaryKey) {
		this.entityService = entityService;
		this.entityType = entityType;
		
		this.keys = keys;
		
		this.streamers = new HashMap<String, CassandraComponentStreamer<?>>();
		for (FieldKey<?> key: this.keys) {
			CassandraComponentStreamer<?> streamer = generateStreamer(key);
			this.streamers.put(key.getName(), streamer);
		}
		
		CassandraComponentStreamer<?> tmpPrimaryStreamer = generateStreamer(primaryKey);
		if (!CassandraPrimaryKeyStreamer.class.isAssignableFrom(tmpPrimaryStreamer.getClass())) {
			ErrorControl.fatal(String.format("Using illegal primary key: %s for: %s", primaryKey.getType().getSimpleName(), entityType.getSimpleName()));
		} else {
			this.primaryStreamer = (CassandraPrimaryKeyStreamer<?>) tmpPrimaryStreamer;
		}
	}

	@SuppressWarnings("unchecked")
	private CassandraComponentStreamer<?> generateStreamer(Key<?> key) {
		CassandraComponentStreamer<?> instance = null;
		
		try {
			Class<?> type = key.getType();
			ParameterizedType subType = key.getSubType();
			
			Class<? extends CassandraComponentStreamer<?>> cls = this.entityService.getStreamer(CassandraConnection.class, CassandraComponentStreamer.class, type);
			Constructor<? extends CassandraComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
		    instance = ctor.newInstance(this.entityService, type, subType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	    
	    return instance;
	}

	public List<ColumnOrSuperColumn> stream(Entity<?> entity) {
		List<ColumnOrSuperColumn> streamed = new ArrayList<ColumnOrSuperColumn>();
		
		long ts = System.currentTimeMillis();
		for (FieldKey<?> key: this.keys) {
			String name = key.getName();

			if (entity.getDirty(key) != null) {
				streamed.add(this.streamers.get(name).write(entity.get(key), Serialization.serialize(name), ts));
			}
		}
		
		return streamed;
	}

	public Map<String, Object> parse(List<ColumnOrSuperColumn> cols) {
		Map<String, Object> parsed = new HashMap<String, Object>();
		
		for (ColumnOrSuperColumn csc: cols) {
			if (csc.column != null) {
				String name = Serialization.deserializeString(csc.column.name);
				parsed.put(name, this.streamers.get(name).read(csc));
			} else {
				String name = Serialization.deserializeString(csc.super_column.name);
				parsed.put(name, this.streamers.get(name).read(csc));
			}
		}
		
		return parsed;
	}
	
	public CassandraPrimaryKeyStreamer<?> getPrimaryStreamer() {
		return this.primaryStreamer;
	}
	
}
