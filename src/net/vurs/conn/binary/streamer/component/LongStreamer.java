package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class LongStreamer extends BinaryComponentStreamer<Long> {

	public LongStreamer(EntityService service, Class<Long> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Long read(BinaryPayload data) {
		return Serialization.deserializeLong(data.read(8));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		return new BinaryPayload(name, ts, Serialization.serialize((Long) comp));
	}

}
