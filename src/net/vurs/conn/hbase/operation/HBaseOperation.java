package net.vurs.conn.hbase.operation;

import net.vurs.conn.hbase.HBaseBackedEntityManager;
import net.vurs.conn.hbase.typesafety.HBaseBackedDefinition;
import net.vurs.entity.Operation;
import net.vurs.transaction.Transaction;

public abstract class HBaseOperation<T extends HBaseBackedDefinition, R, V> extends Operation<R, V> {

	protected HBaseBackedEntityManager<T> manager = null;
	
	public HBaseOperation(HBaseBackedEntityManager<T> manager,
			Transaction transaction) {
		super(transaction);
		this.manager = manager;
	}

}
