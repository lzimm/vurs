package net.vurs.entity.definition.queue;

import java.sql.Timestamp;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.typesafety.FieldKey;

public class NeighbourhoodPingJob extends SQLTable {

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
	public static FieldKey<Entity<Ping>> ping;
	
	@Indexed
	public static FieldKey<Integer> state;
	
	@Indexed
	public static FieldKey<Timestamp> stateTime;
	
	@Indexed
	public static FieldKey<String> stateOwner;
	
}
