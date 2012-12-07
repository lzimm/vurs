package net.vurs.entity.definition.concept;

import net.vurs.conn.sql.typesafety.SQLTable;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Column;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Indexed;
import net.vurs.conn.sql.typesafety.keytypes.annotation.Unique;
import net.vurs.entity.Entity;
import net.vurs.entity.typesafety.FieldKey;

public class Concept extends SQLTable {
	
	public static enum Source {
		USER(1),
		DMOZ(2),
		FREEBASE(3),
		WIKIPEDIA(4);

		Source(Integer index) { this.index = index; }
		private Integer index;
		public Integer getIndex() { return this.index; }
	};
	
	public static enum State {
		ACTIVE(1),
		PREPARING(2);

		State(Integer index) { this.index = index; }
		private Integer index;
		public Integer getIndex() { return this.index; }
	};
	
	@Indexed
	@Unique
	public static FieldKey<String> path;
	
	@Indexed
	public static FieldKey<String> name;
	
	@Indexed
	public static FieldKey<Entity<Concept>> parent;
	
	public static FieldKey<Integer> source;
	public static FieldKey<Integer> state;
	public static FieldKey<String> avatar;
	
	@Column(length=0)
	public static FieldKey<String> description;
	
}
