package net.vurs.entity.definition;

import java.util.List;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.typesafety.CassandraSuperColumn;
import net.vurs.conn.cassandra.typesafety.keytypes.column.StringIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.column.TimeUUIDIndex;
import net.vurs.conn.cassandra.typesafety.keytypes.primary.PrimaryEntity;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityPinged;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.entity.definition.location.Location;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.PrimaryKey;

public class UserData implements CassandraSuperColumn<String, StringIndex, TimeUUID, TimeUUIDIndex> {
	
	public static PrimaryKey<Entity<User>, PrimaryEntity<User>> user;
	
	public static FieldKey<List<Entity<Ping>>> pings;
	public static FieldKey<List<Entity<ActivityPinged>>> activities;
	public static FieldKey<List<Entity<Location>>> locations;
	public static FieldKey<List<Entity<BadgeEarned>>> badges;

}
