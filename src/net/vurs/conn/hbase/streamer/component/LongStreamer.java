package net.vurs.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;
import net.vurs.conn.hbase.streamer.HBaseComponentStreamer;
import net.vurs.conn.hbase.streamer.HBasePrimaryKeyStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class LongStreamer extends HBaseComponentStreamer<Long> implements HBasePrimaryKeyStreamer<Long>, HBaseColumnIndexStreamer<Long> {

	public LongStreamer(EntityService service, Class<Long> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Long read(byte[] data) {
		return Serialization.deserializeLong(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Long) comp);
	}
	
	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
