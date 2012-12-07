package net.vurs.conn.binary.streamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vurs.conn.binary.BinaryConnection;
import net.vurs.conn.binary.BinaryPayload;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.Key;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryEntityStreamer<T extends Definition<?>> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	
	protected Class<T> entityType = null;
	private List<FieldKey<?>> keys = null;
	
	private Map<String, BinaryComponentStreamer<?>> streamers = null;
	
	public BinaryEntityStreamer(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys) {
		this.entityService = entityService;
		this.entityType = entityType;
		
		this.keys = keys;
		
		this.streamers = new HashMap<String, BinaryComponentStreamer<?>>();
		for (FieldKey<?> key: this.keys) {
			BinaryComponentStreamer<?> streamer = generateStreamer(key);
			this.streamers.put(key.getName(), streamer);
		}
	}

	@SuppressWarnings("unchecked")
	private BinaryComponentStreamer<?> generateStreamer(Key<?> key) {
		BinaryComponentStreamer<?> instance = null;
		
		try {
			Class<?> type = key.getType();
			ParameterizedType subType = key.getSubType();
			
			Class<? extends BinaryComponentStreamer<?>> cls = this.entityService.getStreamer(BinaryConnection.class, BinaryComponentStreamer.class, type);
			Constructor<? extends BinaryComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
		    instance = ctor.newInstance(this.entityService, type, subType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	    
	    return instance;
	}

	public BinaryPayload stream(Entity<?> entity) {
		List<byte[]> list = new ArrayList<byte[]>(this.keys.size());
		
		int len = 0;
		long ts = System.currentTimeMillis();
		for (FieldKey<?> key: this.keys) {
			String name = key.getName();

			if (entity.getDirty(key) != null) {
				byte[] bytes = this.streamers.get(name).write(entity.get(key), Serialization.serialize(name), ts).getValue();
				len += bytes.length;
				list.add(bytes);
			}
		}
		
		return BinaryPayload.fromList(Serialization.serialize(entity.getKey()), ts, len, list);
	}

	public Map<String, Object> parse(BinaryPayload data) {
		Map<String, Object> values = new HashMap<String, Object>();
		
		for (FieldKey<?> key: this.keys) {
			String name = key.getName();
			values.put(name, this.streamers.get(name).read(data));
		}
		
		return values;
	}
	
}
