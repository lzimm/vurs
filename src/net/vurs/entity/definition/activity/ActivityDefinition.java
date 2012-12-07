package net.vurs.entity.definition.activity;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.entity.Entity;
import net.vurs.entity.annotation.Prefetch;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.entity.typesafety.FieldKey;

public class ActivityDefinition extends SQLTable {

	public static FieldKey<String> name;
	
	@Prefetch
	public static FieldKey<Entity<Concept>> concept;

}
