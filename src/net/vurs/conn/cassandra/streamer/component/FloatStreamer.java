package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class FloatStreamer extends CassandraComponentStreamer<Float> {

	public FloatStreamer(EntityService service, Class<Float> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Float read(ColumnOrSuperColumn data) {
		return Serialization.deserializeFloat(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize((Float) comp), ts);
		return c;
	}

}
