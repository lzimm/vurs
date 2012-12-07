package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.ProxyEntity;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class EntityStreamer<T extends Definition<?>> extends CassandraComponentStreamer<Entity<T>> implements CassandraPrimaryKeyStreamer<Entity<T>> {

	private Class<T> entityType = null;
	
	@SuppressWarnings("unchecked")
	public EntityStreamer(EntityService service, Class<Entity<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		this.entityType = (Class<T>) subType.getActualTypeArguments()[0];
	}

	@Override
	public Entity<T> read(ColumnOrSuperColumn data) {
		String id = Serialization.deserializeString(data.column.getValue());
		return new ProxyEntity<T>(service, entityType, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize(((Entity<T>) comp).getKey()), ts);
		return c;
	}

	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
