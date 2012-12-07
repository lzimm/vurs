package net.vurs.conn.sql.streamer.component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.ProxyEntity;
import net.vurs.service.definition.EntityService;

public class EntityStreamer<T extends Definition<?>> extends SQLComponentStreamer<Entity<T>> {

	private Class<T> entityType = null;
	
	@SuppressWarnings("unchecked")
	public EntityStreamer(EntityService service, Class<Entity<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		this.entityType = (Class<T>) subType.getActualTypeArguments()[0];
	}

	@Override
	public Entity<T> read(Object data) {
		String id = data.toString();
		return new ProxyEntity<T>(service, entityType, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String write(Object comp, byte[] name, long ts) {
		return ((Entity<T>) comp).getKey();
	}
	
	@Override
	public String generateColumnSchemaType(Field field) {
		return "INT(11)";
	}
	
	@Override
	public String generateColumnSchemaDefaultsAndConstraints(Field field) {
		return "DEFAULT 0";
	}

}
