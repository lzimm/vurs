package net.vurs.entity.definition.badge;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.typesafety.FieldKey;

public class ActivityBadgeCriteria extends SQLTable {
	
	@Indexed
	public static FieldKey<Entity<BadgeDefinition>> badge;
	
	public static FieldKey<Integer> equivalenceOrder;
	
	@Indexed
	public static FieldKey<Entity<ActivityDefinition>> activity;
	public static FieldKey<Integer> pingCount;
	
}
