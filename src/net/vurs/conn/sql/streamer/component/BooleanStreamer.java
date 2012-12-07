package net.vurs.conn.sql.streamer.component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.service.definition.EntityService;

public class BooleanStreamer extends SQLComponentStreamer<Boolean> {

	public BooleanStreamer(EntityService service, Class<Boolean> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String write(Object comp, byte[] name, long ts) {
		return comp.toString();
	}

	@Override
	public String generateColumnSchemaType(Field field) {
		return "TINYINT(1)";
	}

	@Override
	public String generateColumnSchemaDefaultsAndConstraints(Field field) {
		return "DEFAULT 0";
	}
	
}
