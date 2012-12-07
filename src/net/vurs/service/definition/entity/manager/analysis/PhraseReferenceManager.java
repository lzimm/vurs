package net.vurs.service.definition.entity.manager.analysis;

import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.analysis.PhraseReference;
import net.vurs.util.Serialization;

public class PhraseReferenceManager extends CassandraBackedOpenEntityManager<PhraseReference> {
	
	public void train(Entity<ActivityDefinition> activity, String phrase, Entity<Ping> ping) {
		this.insert(activity.getKey(), phrase, Serialization.serialize(ping.getKey()));
	}
	
}
