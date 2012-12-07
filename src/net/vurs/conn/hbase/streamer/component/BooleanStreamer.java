package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class BooleanStreamer extends HBaseComponentStreamer<Boolean> {

	public BooleanStreamer(EntityService service, Class<Boolean> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Boolean read(byte[] data) {
		return Serialization.deserializeBoolean(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Boolean) comp);
	}
	
}
