package net.vurs.service.definition.entity.manager.analysis;

import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.analysis.PhraseInference;
import net.vurs.util.Serialization;

public class PhraseInferenceManager extends CassandraBackedOpenEntityManager<PhraseInference> {
	
	public void train(String phrase, Entity<Ping> ping) {
		this.insert(phrase, ping.get(Ping.activity).getKey(), Serialization.serialize(ping.getKey()));
	}
	
}
