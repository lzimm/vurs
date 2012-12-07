package net.vurs.service.definition.entity.manager.analysis;

import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.PingInterpolation;
import net.vurs.entity.definition.analysis.PhraseInterpolation;
import net.vurs.util.Serialization;

public class PhraseInterpolationManager extends CassandraBackedOpenEntityManager<PhraseInterpolation> {
	
	public void train(String phrase, Entity<PingInterpolation> interpolation, Entity<Ping> ping) {
		this.insert(phrase, interpolation.get(PingInterpolation.relationship), Serialization.serialize(ping.getKey()));
	}
	
}
