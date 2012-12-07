package net.vurs.conn.sql.typesafety;

import net.vurs.conn.sql.typesafety.keytypes.primary.PrimaryInteger;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.util.ClassWalkerFilter.Interface;

public class SQLTable implements SQLBackedDefinition, Interface<SQLBackedDefinition> {

	public static PrimaryKey<Integer, PrimaryInteger> id;
	
}
