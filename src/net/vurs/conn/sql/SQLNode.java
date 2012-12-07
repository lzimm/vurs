package net.vurs.conn.sql;

import java.util.Map;

import net.vurs.entity.Definition;
import net.vurs.entity.Manager;
import net.vurs.entity.Node;

public class SQLNode<T extends Definition<?>> extends Node<T> {

	protected boolean created = false;
	
	public SQLNode(T def, Manager<T> manager, Map<String, Object> map, boolean created) {
		super(def, manager, map);
		this.created = created;
	}
	
	public void markCreated(boolean created) {
		this.created = created;
	}

}
