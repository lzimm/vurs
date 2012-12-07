package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import net.vurs.service.definition.EntityService;

public class TimeUUIDStreamer extends CassandraComponentStreamer<TimeUUID> implements CassandraPrimaryKeyStreamer<TimeUUID>, CassandraColumnIndexStreamer<TimeUUID> {

	public TimeUUIDStreamer(EntityService service, Class<TimeUUID> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public TimeUUID read(ColumnOrSuperColumn data) {
		return TimeUUID.fromBytes(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, ((TimeUUID) comp).getBytes(), ts);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "TimeUUIDType";
	}

	@Override
	public String generateKey() {
		return (new TimeUUID()).toString();
	}

}
