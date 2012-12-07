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

public class StringStreamer extends CassandraComponentStreamer<String> implements CassandraPrimaryKeyStreamer<String>, CassandraColumnIndexStreamer<String> {

	public StringStreamer(EntityService service, Class<String> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String read(ColumnOrSuperColumn data) {
		return Serialization.deserializeString(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize((String) comp), ts);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "UTF8Type";
	}
	
	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
