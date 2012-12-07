package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.common.TimeUUID;
import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.streamer.HBasePrimaryKeyStreamer;
import net.vurs.service.definition.EntityService;

public class TimeUUIDStreamer extends HBaseComponentStreamer<TimeUUID> implements HBasePrimaryKeyStreamer<TimeUUID>, HBaseColumnIndexStreamer<TimeUUID> {

	public TimeUUIDStreamer(EntityService service, Class<TimeUUID> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public TimeUUID read(byte[] data) {
		return TimeUUID.fromBytes(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return ((TimeUUID) comp).getBytes();
	}

	@Override
	public String generateKey() {
		return (new TimeUUID()).toString();
	}

}
