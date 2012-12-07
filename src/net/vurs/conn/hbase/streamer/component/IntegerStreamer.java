package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.Serialization;

public class IntegerStreamer extends HBaseComponentStreamer<Integer> implements HBaseColumnIndexStreamer<Integer> {

	public IntegerStreamer(EntityService service, Class<Integer> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Integer read(byte[] data) {
		return Serialization.deserializeInt(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Integer) comp);
	}

}
