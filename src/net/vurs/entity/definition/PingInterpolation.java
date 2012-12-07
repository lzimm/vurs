package net.vurs.entity.definition;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class PingInterpolation implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> uuid;
	
	public static FieldKey<Entity<Ping>> ping;
	public static FieldKey<String> relationship;	
	public static FieldKey<String> value;	

}
