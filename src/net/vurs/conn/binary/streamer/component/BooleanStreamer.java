package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class BooleanStreamer extends BinaryComponentStreamer<Boolean> {
	private static int BOOLEAN_SIZE = Serialization.serialize(Boolean.TRUE).length;
	
	public BooleanStreamer(EntityService service, Class<Boolean> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Boolean read(BinaryPayload data) {
		return Serialization.deserializeBoolean(data.read(BOOLEAN_SIZE));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		return new BinaryPayload(name, ts, Serialization.serialize((Boolean) comp));
	}
	
}
