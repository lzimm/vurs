package net.vurs.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.entity.typesafety.FieldKey;
import net.vurs.entity.typesafety.FieldKey.FetchType;
import net.vurs.entity.typesafety.PrimaryKey;
import net.vurs.service.definition.EntityService;
import net.vurs.transaction.Transaction;
import net.vurs.util.ErrorControl;

public abstract class Manager<T extends Definition<?>> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService;
	protected Class<T> entityType = null;
	protected T def = null;

	protected List<FieldKey<?>> entityKeys = null;
	protected PrimaryKey<?, ?> primaryKey = null;
	
	protected EntityHelper<T> entityHelper = null;
	
	protected List<FieldKey<?>> prefetched = null;
	
	@SuppressWarnings("unchecked")
	public void setup(EntityService entityService,
			Class<T> entityType, List<FieldKey<?>> keys,
			PrimaryKey<?, ?> primaryKey) {
		this.entityService = entityService;
		this.entityType = entityType;
		this.entityKeys = keys;
		this.primaryKey = primaryKey;
		
		this.entityHelper = new EntityHelper<T>();
		
		this.prefetched = new ArrayList<FieldKey<?>>();
		for (FieldKey<?> k: keys) {
			if (!k.prefetch().equals(FetchType.NONE)) {
				this.prefetched.add((FieldKey<Entity<?>>) k);
				this.logger.info(String.format("Added %s to prefetch list", k.getName()));
			}
		}
		
		try {
			this.def = entityType.newInstance();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public EntityService getEntityService() { return this.entityService; }
	public List<FieldKey<?>> getKeys() { return this.entityKeys; }
	public PrimaryKey<?, ?> getPrimaryKey() { return this.primaryKey; }
	public T getDefinition() { return this.def; }
	public List<FieldKey<?>> getPrefetched() { return this.prefetched; }
		
	public void materialize(List<Entity<T>> proxies) {
		List<String> keys = new ArrayList<String>(proxies.size());
		for (Entity<T> e: proxies) keys.add(e.getKey());
		
		Map<String, Entity<T>> materialized = this.get(keys);
		for (int i = 0; i < proxies.size(); i++) {
			proxies.get(i).node = materialized.get(proxies.get(i).getKey()).node;
		}
	}
	
	public abstract Entity<T> getProxy(String key);
	
	public abstract Entity<T> wrap(String key, Node<T> node);
	
	public Entity<T> create() { return create(null); }
	public abstract Entity<T> create(String key);
	
	public Entity<T> get(String key) { return this.get(Arrays.asList(key)).get(key); }
	public Map<String, Entity<T>> get(List<String> keys) { return get(keys, this.entityService.getGlobalTransaction()).getValue(); }
	public abstract Operation<Boolean, Map<String, Entity<T>>> get(List<String> keys, Transaction transaction);

	public boolean save(Entity<T> entity) { return save(entity, this.entityService.getGlobalTransaction()).getResult(); }
	public abstract Operation<Boolean, Void> save(Entity<T> entity, Transaction transaction);

	public void delete(String key) { delete(key, this.entityService.getGlobalTransaction()).execute(); }
	public abstract Operation<Boolean, Void> delete(String key, Transaction transaction);
	
	public EntityHelper<T> getHelper() { return this.entityHelper; }
	public List<String> getKeys(List<Entity<T>> entities) { return this.entityHelper.getKeys(entities); }
	public <K> List<K> getValues(FieldKey<K> key, List<Entity<T>> entities) { return this.entityHelper.getValues(key, entities); }
	
}