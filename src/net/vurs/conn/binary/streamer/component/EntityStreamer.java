package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.conn.cassandra.typesafety.CassandraBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.entity.ProxyEntity;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class EntityStreamer<T extends CassandraBackedDefinition> extends BinaryComponentStreamer<Entity<T>> {

	private Class<T> entityType = null;
	
	@SuppressWarnings("unchecked")
	public EntityStreamer(EntityService service, Class<Entity<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		this.entityType = (Class<T>) subType.getActualTypeArguments()[0];
	}

	@Override
	public Entity<T> read(BinaryPayload data) {
		int len = Serialization.deserializeInt(data.read(4));
		String id = Serialization.deserializeString(data.read(len));
		return new ProxyEntity<T>(service, entityType, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		byte[] bytes = Serialization.serialize(((Entity<T>) comp).getKey());
		byte[] len = Serialization.serialize(bytes.length);
		return new BinaryPayload(name, ts, len, bytes);
	}

}
