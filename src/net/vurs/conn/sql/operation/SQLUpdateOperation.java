package net.vurs.conn.sql.operation;

import java.util.Map;
import java.util.Map.Entry;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;
import net.vurs.entity.Entity;
import net.vurs.transaction.Transaction;

public class SQLUpdateOperation<T extends SQLBackedDefinition> extends SQLSaveOperation<T> {
	
	public SQLUpdateOperation(SQLBackedEntityManager<T> manager, Transaction transaction, Entity<T> entity) {
		super(manager, transaction, entity);
	}

	@Override
	public boolean runQuery(SQLConnection conn) {
		StringBuilder values = new StringBuilder();
		
		Map<String, String> streamed = this.manager.getStreamer().stream(this.entity);
		for (Entry<String, String> e: streamed.entrySet()) {
			if (e.getKey().equals(this.manager.getPrimaryKey().getName())) continue;

			values.append('`')
					.append(e.getKey())
					.append("` = '")
					.append(e.getValue())
					.append("',");
		}
		
		String valuePart = values.substring(0, values.length() - 1);
		
		StringBuilder query = new StringBuilder(this.manager.updateStart())
										.append(valuePart)
										.append(" WHERE `")
										.append(this.manager.getPrimaryKey().getName())
										.append("` = '")
										.append(this.entity.getKey())
										.append("'");
		
		return conn.update(query.toString()) > 0;
	}

}
