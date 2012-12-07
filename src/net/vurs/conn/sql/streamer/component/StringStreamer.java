package net.vurs.conn.sql.streamer.component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.streamer.SQLPrimaryKeyStreamer;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Column;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;

public class StringStreamer extends SQLPrimaryKeyStreamer<String> {

	public StringStreamer(EntityService service, Class<String> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public String write(Object comp, byte[] name, long ts) {
		return (String) comp;
	}

	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;
	}
	
	@Override
	public String generateColumnSchemaType(Field field) {
		Column c = field.getAnnotation(Column.class);
		if (c != null) {
			if (c.length() > -1) {
				if (c.length() == 0) {
					return "TEXT";
				} else {
					return String.format("VARCHAR(%s)", c.length());
				}
			}
		}
		
		return "VARCHAR(255)";
	}
	
	@Override
	public String generateColumnSchemaDefaultsAndConstraints(Field field) {
		return "DEFAULT ''";
	}

	@Override
	public String generateColumnSchemaPrimaryKeyModifier(Field field) {
		ErrorControl.fatal(String.format("Attempted to use %s as primary key", this.type.getName()));
		return null;
	}

}
