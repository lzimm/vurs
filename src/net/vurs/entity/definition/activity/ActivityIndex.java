package net.vurs.entity.definition.activity;

import java.util.List;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraSuperColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.column.TimeUUIDIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryString;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class ActivityIndex implements CassandraSuperColumn<String, StringIndex, TimeUUID, TimeUUIDIndex> {
	
	public static PrimaryKey<String, PrimaryString> token;
	
	public static FieldKey<List<Entity<ActivityDefinition>>> activities;

}
