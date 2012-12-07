package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class FloatStreamer extends BinaryComponentStreamer<Float> {

	public FloatStreamer(EntityService service, Class<Float> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Float read(BinaryPayload data) {
		return Serialization.deserializeFloat(data.read(4));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		return new BinaryPayload(name, ts, Serialization.serialize((Float) comp));
	}

}
