package net.vurs.conn.sql.streamer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Unique;
import net.vurs.entity.ComponentStreamer;
import net.vurs.service.definition.EntityService;

public abstract class SQLComponentStreamer<C> extends ComponentStreamer<C, Object, String> {

	protected EntityService service = null;
	protected Class<C> type = null;
	protected ParameterizedType subType = null;
	
	public SQLComponentStreamer(EntityService service, Class<C> type, ParameterizedType subType) {
		this.service = service;
		this.type = type;
		this.subType = subType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public C read(Object comp) {
		return (C) comp;
	}
	
	public String generateColumnSchema(Class<? extends SQLBackedDefinition> definition, Field field) {
		return String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s NOT NULL %s;", 
				definition.getSimpleName().toLowerCase(), 
				field.getName(),
				generateColumnSchemaType(field),
				generateColumnSchemaDefaultsAndConstraints(field));
	}
	
	public abstract String generateColumnSchemaType(Field field);
	public abstract String generateColumnSchemaDefaultsAndConstraints(Field field);

	public String generateAnnotationSchema(Class<? extends SQLBackedDefinition> definition, Field field) {
		StringBuilder builder = new StringBuilder();
		
		for (Annotation a: field.getAnnotations()) {
			if (a.annotationType().equals(Unique.class)) {
				builder.append(String.format("ALTER TABLE `%s` ADD UNIQUE KEY `unique_%s` (`%s`);\n",
						definition.getSimpleName().toLowerCase(), 
						field.getName(),
						field.getName()));
			}
			
			if (a.annotationType().equals(Indexed.class)) {
				builder.append(String.format("ALTER TABLE `%s` ADD INDEX `index_%s` (`%s`);\n",
						definition.getSimpleName().toLowerCase(), 
						field.getName(),
						field.getName()));
			}
		}
		
		return builder.toString();
	}
	
}
