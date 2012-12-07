package net.vurs.entity.definition.analysis;

import java.util.List;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraOpenSuperColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.column.TimeUUIDIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryEntity;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.typesafety.PrimaryKey;

public class PhraseReference implements CassandraOpenSuperColumn<String, StringIndex, TimeUUID, TimeUUIDIndex, List<Entity<Ping>>> {

	/*
	 * Activity:key = {
	 * 		*<Phrase:key = {
	 * 			<ListOfPings>
	 * 		}>
	 * }
	 */
	
	public static PrimaryKey<Entity<ActivityDefinition>, PrimaryEntity<ActivityDefinition>> activity;
	
}
