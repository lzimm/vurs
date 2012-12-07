package net.vurs.entity.definition.analysis;

import java.util.List;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraOpenSuperColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.column.TimeUUIDIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryString;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.typesafety.PrimaryKey;

public class PhraseInference implements CassandraOpenSuperColumn<String, StringIndex, TimeUUID, TimeUUIDIndex, List<Entity<Ping>>> {
	
	/*
	 * Phrase:key = {
	 * 		*<Activity:key = {
	 * 			<ListOfPings>
	 * 		}>
	 * }
	 */
	
	public static PrimaryKey<String, PrimaryString> phrase;
	
}
