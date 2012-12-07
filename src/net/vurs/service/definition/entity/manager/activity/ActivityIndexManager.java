package net.vurs.service.definition.entity.manager.activity;

import java.util.List;

import net.vurs.conn.cassandra.CassandraBackedEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.activity.ActivityIndex;

public class ActivityIndexManager extends CassandraBackedEntityManager<ActivityIndex> {

	public void index(List<String> tokens, Entity<ActivityDefinition> activity) {		
		for (String token: tokens) {
			this.insert(token, ActivityIndex.activities, activity);
		}
	}
	
}
