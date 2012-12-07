package net.vurs.entity.definition.badge;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;

public class BadgeDefinition extends SQLTable {
	
	public static enum Type {
		LOGICAL(1),
		ACTIVITY(2);

		Type(Integer index) { this.index = index; }
		private Integer index;
		public Integer getIndex() { return this.index; }
	};
	
	@Indexed
	public static FieldKey<Entity<BadgeDefinition>> parent;
	
	public static FieldKey<String> name;
	public static FieldKey<String> description;
	public static FieldKey<String> asset;
	
	public static FieldKey<Integer> type;

}
