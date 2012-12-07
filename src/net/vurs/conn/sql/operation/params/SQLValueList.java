package net.vurs.conn.sql.operation.params;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.typesafety.Key;

public class SQLValueList<T extends SQLBackedDefinition> {

	private List<SQLValue<T, ?>> values = null;
	
	public SQLValueList() {
		this.values = new ArrayList<SQLValue<T, ?>>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SQLValueList(SQLValue... values) {
		this.values = new ArrayList<SQLValue<T, ?>>(values.length);
		for (SQLValue value: values) {
			this.values.add(value);
		}
	}

	public <K> SQLValueList<T> add(SQLValue<T, K> value) {
		this.values.add(value);
		return this;
	}
	
	public <K> SQLValueList<T> add(Key<K> key, K value) {
		this.values.add(new SQLValue<T, K>(key, value));
		return this;
	}
	
	public String stream(SQLBackedEntityManager<T> manager) {
		StringBuilder streamed = new StringBuilder();
		
		Iterator<SQLValue<T, ?>> itr = this.values.iterator();
		while (itr.hasNext()) {
			SQLValue<T, ?> value = itr.next();

			streamed.append('`').append(value.streamKey(manager)).append("` = '").append(value.streamValue(manager)).append('\'');
			
			if (itr.hasNext()) {
				streamed.append(',');
			}
		}
		
		return streamed.toString();
	}
	
}
