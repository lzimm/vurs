package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class DoubleStreamer extends BinaryComponentStreamer<Double> {

	public DoubleStreamer(EntityService service, Class<Double> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Double read(BinaryPayload data) {
		return Serialization.deserializeDouble(data.read(8));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		return new BinaryPayload(name, ts, Serialization.serialize((Double) comp));
	}

}
