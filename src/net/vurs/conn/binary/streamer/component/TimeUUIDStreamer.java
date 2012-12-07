package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.common.TimeUUID;
import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;

public class TimeUUIDStreamer extends BinaryComponentStreamer<TimeUUID> {

	public TimeUUIDStreamer(EntityService service, Class<TimeUUID> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public TimeUUID read(BinaryPayload data) {
		return TimeUUID.fromBytes(data.read(16));
	}

	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		return new BinaryPayload(name, ts, ((TimeUUID) comp).getBytes());
	}

}
