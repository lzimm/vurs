package net.vurs.service.definition.entity.manager.activity;

import java.util.List;

import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.activity.ActivityHierarchy;

public class ActivityHierarchyManager extends CassandraBackedEntityManager<ActivityHierarchy> {

	public void create(Entity<ActivityDefinition> activity, List<Entity<ActivityDefinition>> parents) {		
		for (Entity<ActivityDefinition> parent: parents) {
			this.insert(activity.getKey(), ActivityHierarchy.parents, parent);
			this.insert(parent.getKey(), ActivityHierarchy.children, activity);
		}
	}
	
}
