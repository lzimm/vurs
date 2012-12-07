package net.vurs.conn.hbase.streamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import net.vurs.conn.hbase.HBaseConnection;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.Key;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseEntityStreamer<T extends HBaseBackedDefinition> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	
	protected Class<T> entityType = null;
	protected List<FieldKey<?>> keys = null;
	
	protected Map<String, HBaseComponentStreamer<?>> streamers = null;
	protected HBasePrimaryKeyStreamer<?> primaryStreamer = null;
	
	public HBaseEntityStreamer(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys, PrimaryKey<?, ?> primaryKey) {
		this.entityService = entityService;
		this.entityType = entityType;
		
		this.keys = keys;
		
		this.streamers = new HashMap<String, HBaseComponentStreamer<?>>();
		for (FieldKey<?> key: this.keys) {
			HBaseComponentStreamer<?> streamer = generateStreamer(key);
			this.streamers.put(key.getName(), streamer);
		}
		
		HBaseComponentStreamer<?> tmpPrimaryStreamer = generateStreamer(primaryKey);
		if (!HBasePrimaryKeyStreamer.class.isAssignableFrom(tmpPrimaryStreamer.getClass())) {
			ErrorControl.fatal(String.format("Using illegal primary key: %s for: %s", primaryKey.getType().getSimpleName(), entityType.getSimpleName()));
		} else {
			this.primaryStreamer = (HBasePrimaryKeyStreamer<?>) tmpPrimaryStreamer;
		}
	}
	
	@SuppressWarnings("unchecked")
	private HBaseComponentStreamer<?> generateStreamer(Key<?> key) {
		HBaseComponentStreamer<?> instance = null;
		
		try {
			Class<?> type = key.getType();
			ParameterizedType subType = key.getSubType();
			
			Class<? extends HBaseComponentStreamer<?>> cls = this.entityService.getStreamer(HBaseConnection.class, HBaseComponentStreamer.class, type);
			Constructor<? extends HBaseComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
		    instance = ctor.newInstance(this.entityService, type, subType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	    
	    return instance;
	}

	public List<KeyValue> stream(Entity<?> entity) {
		List<KeyValue> streamed = new ArrayList<KeyValue>();
		
		byte[] keyBytes = Serialization.serialize(entity.getKey());
		byte[] qualifierBytes = new byte[0];
		
		long ts = System.currentTimeMillis();
		for (FieldKey<?> key: this.keys) {
			String name = key.getName();

			if (entity.getDirty(key) != null) {
				byte[] familyBytes = Serialization.serialize(key.getName());
				byte[] valueBytes = this.streamers.get(name).write(entity.get(key), Serialization.serialize(name), ts);
				KeyValue kv = new KeyValue(keyBytes, familyBytes, qualifierBytes, valueBytes);
				streamed.add(kv);
			}
		}
		
		return streamed;
	}

	public Map<String, Object> parse(Result result) {
		Map<String, Object> parsed = new HashMap<String, Object>();
		
		byte[] qualifierBytes = new byte[0];
		for (Entry<byte[], NavigableMap<byte[], byte[]>> e: result.getNoVersionMap().entrySet()) {
			String name = Serialization.deserializeString(e.getKey());
			parsed.put(name, this.streamers.get(name).read(e.getValue().get(qualifierBytes)));
		}
		
		return parsed;
	}
	
	public HBasePrimaryKeyStreamer<?> getPrimaryStreamer() {
		return this.primaryStreamer;
	}

}
