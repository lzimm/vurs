package net.vurs.entity.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.vurs.common.Triple;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;
import net.vurs.entity.Operation;
import net.vurs.entity.operation.interfaces.PrefetchableOperation;
import net.vurs.entity.typesafety.FieldKey;
import net.vurs.transaction.Transaction;

public class PrefetchOperation<T extends Definition<?>> extends Operation<Boolean, Void> {
	
	private PrefetchableOperation<T> fetchable = null;

	public PrefetchOperation(Transaction transaction, PrefetchableOperation<T> fetchable) {
		super(transaction);
		this.fetchable = fetchable;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		for (FieldKey<?> k: this.fetchable.getManager().getPrefetched()) {
			List proxies = new ArrayList();
			
			switch (k.prefetch()) {
				case ENTITY:
					for (Entity<T> entity: this.fetchable.getEntities()) {
						proxies.add(entity.get(k));
					}
				break;
				
				case LIST:
					for (Entity<T> entity: this.fetchable.getEntities()) {
						proxies.addAll((Collection) entity.get(k));
					}
				break;
			}
			
			if (proxies.size() > 0) {
				Manager<? extends Definition<?>> manager = this.fetchable.getManager().getEntityService().getManager((Class<? extends Definition<?>>) k.getSubType().getActualTypeArguments()[0]);
				manager.materialize(proxies);
			}
		}
		
		return new Triple<Boolean, Boolean,Void>(true, true, null);
	}

}
