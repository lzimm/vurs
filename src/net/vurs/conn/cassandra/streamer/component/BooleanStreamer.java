package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class BooleanStreamer extends CassandraComponentStreamer<Boolean> {

	public BooleanStreamer(EntityService service, Class<Boolean> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Boolean read(ColumnOrSuperColumn data) {
		return Serialization.deserializeBoolean(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize((Boolean) comp), ts);
		return c;
	}
	
}
