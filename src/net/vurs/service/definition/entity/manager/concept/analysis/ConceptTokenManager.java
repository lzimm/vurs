package net.vurs.service.definition.entity.manager.concept.analysis;

import net.vurs.conn.cassandra.CassandraBackedOpenEntityManager;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.concept.ConceptLink;
import net.vurs.entity.definition.concept.analysis.ConceptToken;
import net.vurs.util.Serialization;

public class ConceptTokenManager extends CassandraBackedOpenEntityManager<ConceptToken> {
	
	public void train(String phrase, Entity<ConceptLink> link) {
		this.insert(phrase, link.get(ConceptLink.concept).getKey(), Serialization.serialize(link.getKey()));
	}
	
}