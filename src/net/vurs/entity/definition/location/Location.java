package net.vurs.entity.definition.location;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class Location implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> uuid;
	
	public static FieldKey<String> name;
	public static FieldKey<String> type;
	
	public static FieldKey<Double> lat;
	public static FieldKey<Double> lon;

}
