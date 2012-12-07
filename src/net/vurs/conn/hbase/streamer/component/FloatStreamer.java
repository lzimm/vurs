package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class FloatStreamer extends HBaseComponentStreamer<Float> {

	public FloatStreamer(EntityService service, Class<Float> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Float read(byte[] data) {
		return Serialization.deserializeFloat(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Float) comp);
	}

}
