package net.vurs.conn.sql.streamer.component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.service.definition.EntityService;

public class LongStreamer extends SQLComponentStreamer<Long> {

	public LongStreamer(EntityService service, Class<Long> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String write(Object comp, byte[] name, long ts) {
		return comp.toString();
	}
	
	@Override
	public String generateColumnSchemaType(Field field) {
		return "INT(11)";
	}
	
	@Override
	public String generateColumnSchemaDefaultsAndConstraints(Field field) {
		return "DEFAULT 0";
	}

}
