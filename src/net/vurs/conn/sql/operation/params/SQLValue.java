package net.vurs.conn.sql.operation.params;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.typesafety.Key;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.util.EscapeUtil;
import net.vurs.util.Serialization;

public class SQLValue<T extends SQLBackedDefinition, K> {
	
	private Key<K> key = null;
	private K value = null;
	
	public SQLValue(Key<K> key, K value) {
		this.key = key;
		this.value = value;
	}
	
	public String streamKey(SQLBackedEntityManager<T> manager) {
		return this.key.getName();
	}
	
	public String streamValue(SQLBackedEntityManager<T> manager) {
		String streamed = null;
		
		if (PrimaryKey.class.isAssignableFrom(key.getClass())) {
			streamed = manager.getStreamer().getPrimaryStreamer()
						.write(this.value, Serialization.serialize(key.getName()), System.currentTimeMillis());
		} else {
			streamed = manager.getStreamer().getStreamer(key.getName())
						.write(this.value, Serialization.serialize(key.getName()), System.currentTimeMillis());
		}
		
		return EscapeUtil.escape(streamed, EscapeUtil.BACKSLASH, EscapeUtil.MYSQL_CHARS);
	}

}
