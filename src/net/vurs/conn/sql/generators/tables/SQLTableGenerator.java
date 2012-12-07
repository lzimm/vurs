package net.vurs.conn.sql.generators.tables;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import net.vurs.conn.sql.generators.SQLSchemaTableGenerator;
import net.vurs.conn.sql.streamer.SQLComponentStreamer;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.util.Reflection;

public class SQLTableGenerator extends SQLSchemaTableGenerator<SQLTable> {

	@Override
	protected void generate(Class<? extends SQLBackedDefinition> definition, PrintStream out) {
		out.println(String.format("\n\n\nCREATE TABLE `%s` (", definition.getSimpleName().toLowerCase()));

		for (Field f: definition.getFields()) {
			ParameterizedType field = (ParameterizedType) f.getGenericType();
			if (field.getRawType().equals(PrimaryKey.class)) {
				Class<?> fieldType = Reflection.getParamType(field, 0);
				ParameterizedType fieldTypeParam = Reflection.getParamSubType(field, 0);
				
				SQLComponentStreamer<?> streamer = this.getStreamer(fieldType, fieldTypeParam);
				out.println(streamer.generateColumnSchema(definition, f));			
			}
		}
		
		out.println(String.format(") ENGINE=MyISAM DEFAULT CHARSET=latin1;\n"));
		
		for (Field f: definition.getFields()) {
			ParameterizedType field = (ParameterizedType) f.getGenericType();
			if (field.getRawType().equals(PrimaryKey.class)) continue;
			
			Class<?> fieldType = Reflection.getParamType(field, 0);
			ParameterizedType fieldTypeParam = Reflection.getParamSubType(field, 0);
			
			SQLComponentStreamer<?> streamer = this.getStreamer(fieldType, fieldTypeParam);
			
			out.println(streamer.generateColumnSchema(definition, f));
			out.print(streamer.generateAnnotationSchema(definition, f));
		}
	}

}
