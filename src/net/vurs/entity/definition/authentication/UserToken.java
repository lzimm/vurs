package net.vurs.entity.definition.authentication;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class UserToken implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> token;
	
	public static FieldKey<String> key;
	public static FieldKey<Entity<User>> user;

}
