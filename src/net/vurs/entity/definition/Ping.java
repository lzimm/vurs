package net.vurs.entity.definition;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.location.Location;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class Ping implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> uuid;
	
	public static FieldKey<Entity<User>> user;
	
	public static FieldKey<String> data;
	
	public static FieldKey<Double> lat;
	public static FieldKey<Double> lon;
	
	public static FieldKey<String> description;	
	
	public static FieldKey<Entity<ActivityDefinition>> activity;
	public static FieldKey<Entity<Location>> location;

}
