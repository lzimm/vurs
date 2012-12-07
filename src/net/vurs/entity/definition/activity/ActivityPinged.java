package net.vurs.entity.definition.activity;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.User;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class ActivityPinged implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> uuid;
	
	public static FieldKey<Entity<Ping>> ping;
	public static FieldKey<Entity<User>> user;
	public static FieldKey<Entity<ActivityDefinition>> definition;

}
