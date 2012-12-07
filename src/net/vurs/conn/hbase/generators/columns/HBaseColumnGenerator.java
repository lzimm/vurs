package net.vurs.conn.hbase.generators.columns;

import java.lang.reflect.ParameterizedType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.vurs.conn.hbase.generators.HBaseSchemaColumnGenerator;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.conn.hbase.typesafety.HBaseColumn;
import net.vurs.entity.typesafety.FieldKey;

public class HBaseColumnGenerator extends HBaseSchemaColumnGenerator<HBaseColumn<?, ?>> {

	@Override
	public void generate(Class<? extends HBaseBackedDefinition> def, ParameterizedType definitionType, Document xmldoc, Element column) {
		for(FieldKey<?> k: this.entityService.getManager(def).getKeys()) {
			Element field = xmldoc.createElement("Column");
			field.setAttribute("Name", k.getName());
			column.appendChild(field);
		}
	}

}
