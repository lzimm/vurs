package net.vurs.conn.sql.operation.predicate;

import java.util.Arrays;
import java.util.Iterator;

import net.vurs.conn.sql.SQLBackedEntityManager;
import net.vurs.conn.sql.typesafety.SQLBackedDefinition;

public class SQLOrPredicate<T extends SQLBackedDefinition> extends SQLPredicate<T> {

	private SQLPredicate<T>[] predicates = null;;
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public SQLOrPredicate(SQLPredicate... predicates) {
		this.predicates = predicates;
	}
	
	@Override
	public String stream(SQLBackedEntityManager<T> manager) {
		StringBuilder streamed = new StringBuilder().append('(');
		
		Iterator<SQLPredicate<T>> itr = Arrays.asList(this.predicates).iterator();
		while (itr.hasNext()) {
			SQLPredicate<T> predicate = itr.next();
			streamed.append(predicate.stream(manager));
			if (itr.hasNext()) {
				streamed.append(" OR ");
			}
		}
		
		streamed.append(')');
		
		return streamed.toString();
	}

}
