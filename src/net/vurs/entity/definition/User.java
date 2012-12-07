package net.vurs.entity.definition;

/*
import net.vurs.conn.cassandra.typesafety.CassandraColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryString;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class User implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<String, PrimaryString> name;
	
	public static FieldKey<String> password;
	public static FieldKey<String> email;
	public static FieldKey<Boolean> isAdmin;

}
*/

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Unique;
import net.vurs.entity.typesafety.FieldKey;

public class User extends SQLTable {
	
	@Indexed
	@Unique
	public static FieldKey<String> name;
	
	public static FieldKey<String> password;
	public static FieldKey<String> email;
	public static FieldKey<Boolean> isAdmin;

}
