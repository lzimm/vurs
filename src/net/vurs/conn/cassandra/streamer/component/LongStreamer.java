package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import net.vurs.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class LongStreamer extends CassandraComponentStreamer<Long> implements CassandraPrimaryKeyStreamer<Long>, CassandraColumnIndexStreamer<Long> {

	public LongStreamer(EntityService service, Class<Long> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Long read(ColumnOrSuperColumn data) {
		return Serialization.deserializeLong(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize((Long) comp), ts);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "LongType";
	}
	
	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
