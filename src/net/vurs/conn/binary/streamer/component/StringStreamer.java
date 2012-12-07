package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class StringStreamer extends BinaryComponentStreamer<String> {

	public StringStreamer(EntityService service, Class<String> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String read(BinaryPayload data) {
		int len = Serialization.deserializeInt(data.read(4));
		return Serialization.deserializeString(data.read(len));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		byte[] bytes = Serialization.serialize((String) comp);
		byte[] len = Serialization.serialize(bytes.length);
		return new BinaryPayload(name, ts, len, bytes);
	}

}
