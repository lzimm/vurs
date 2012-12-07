package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class DoubleStreamer extends HBaseComponentStreamer<Double> {

	public DoubleStreamer(EntityService service, Class<Double> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Double read(byte[] data) {
		return Serialization.deserializeDouble(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Double) comp);
	}

}
