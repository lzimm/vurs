package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.streamer.HBasePrimaryKeyStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.ProxyEntity;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class EntityStreamer<T extends Definition<?>> extends HBaseComponentStreamer<Entity<T>> implements HBasePrimaryKeyStreamer<Entity<T>> {

	private Class<T> entityType = null;
	
	@SuppressWarnings("unchecked")
	public EntityStreamer(EntityService service, Class<Entity<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		this.entityType = (Class<T>) subType.getActualTypeArguments()[0];
	}

	@Override
	public Entity<T> read(byte[] data) {
		String id = Serialization.deserializeString(data);
		return new ProxyEntity<T>(service, entityType, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize(((Entity<T>) comp).getKey());
	}

	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
