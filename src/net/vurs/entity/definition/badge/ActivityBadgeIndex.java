package net.vurs.entity.definition.badge;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.typesafety.FieldKey;

public class ActivityBadgeIndex extends SQLTable {

	@Indexed
	public static FieldKey<Entity<ActivityDefinition>> activity;
	
	@Indexed
	public static FieldKey<Entity<BadgeDefinition>> badge;
	
}
