package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class IntegerStreamer extends CassandraComponentStreamer<Integer> implements CassandraColumnIndexStreamer<Integer> {

	public IntegerStreamer(EntityService service, Class<Integer> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Integer read(ColumnOrSuperColumn data) {
		return Serialization.deserializeInt(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize((Integer) comp), ts);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "LongType";
	}

}
