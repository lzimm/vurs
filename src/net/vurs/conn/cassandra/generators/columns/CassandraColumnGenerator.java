package net.vurs.conn.cassandra.generators.columns;

import java.lang.reflect.ParameterizedType;

import org.w3c.dom.Element;

import net.vurs.conn.cassandra.generators.CassandraSchemaColumnGenerator;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.util.Reflection;

public class CassandraColumnGenerator extends CassandraSchemaColumnGenerator<CassandraColumn<?, ?>> {

	@Override
	public void generate(ParameterizedType def, Element column) {
		Class<?> type = Reflection.getParamType(def, 0);
		ParameterizedType typeParam = Reflection.getParamSubType(def, 0);
		String compareType = this.getStreamer(type, typeParam).compareWithType();
		
		column.setAttribute("CompareWith", compareType);
	}

}
