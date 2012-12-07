package net.vurs.entity.definition.concept;

import java.sql.Timestamp;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Column;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;

public class ConceptLink extends SQLTable {

	public static enum State {
		PROCESSED(1),
		PROCESSING(2),
		WAITING(3),
		ERROR(4);

		State(Integer index) { this.index = index; }
		private Integer index;
		public Integer getIndex() { return this.index; }
	};
	
	@Indexed
	public static FieldKey<Entity<Concept>> concept;
	
	@Indexed
	public static FieldKey<Integer> state;
	
	@Indexed
	public static FieldKey<Timestamp> stateTime;
	
	@Indexed
	public static FieldKey<String> stateOwner;
	
	@Column(length=512)
	public static FieldKey<String> link;
	
}
