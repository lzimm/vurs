package net.vurs.entity;

import net.vurs.service.definition.EntityService;

public class ProxyEntity<T extends Definition<?>> extends Entity<T> {
	
	public ProxyEntity(EntityService service, Class<T> type, String key) {
		super(service, type, key, null);
	}
	
	@Override
	public Node<T> getNode() {
		if (this.node == null) {
			this.node = this.manager.get(this.key).getNode();
		}
		
		return this.node;
	}

}
