package net.vurs.entity;

import net.vurs.conn.Connection;
import net.vurs.service.definition.EntityService;

public class OpenEntity<T extends Definition<? extends Connection>> extends Entity<T> {

	public OpenEntity(EntityService service, Class<T> type, String key,
			Node<T> node) {
		super(service, type, key, node);
	}

}
