package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.streamer.HBasePrimaryKeyStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class StringStreamer extends HBaseComponentStreamer<String> implements HBasePrimaryKeyStreamer<String>, HBaseColumnIndexStreamer<String> {

	public StringStreamer(EntityService service, Class<String> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String read(byte[] data) {
		return Serialization.deserializeString(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((String) comp);
	}
	
	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
