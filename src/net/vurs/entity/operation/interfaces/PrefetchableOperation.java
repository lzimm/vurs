package net.vurs.entity.operation.interfaces;

import java.util.Collection;

import net.vurs.entity.Definition;
import net.vurs.entity.Entity;
import net.vurs.entity.Manager;

public interface PrefetchableOperation<T extends Definition<?>> {
	
	public Manager<T> getManager();
	public Collection<? extends Entity<T>> getEntities();
	
}
