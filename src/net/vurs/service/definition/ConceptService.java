package net.vurs.service.definition;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.TreeMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.ConstructingHashMap;
import net.vurs.common.Pair;
import net.vurs.common.Ranking;
import net.vurs.common.constructor.AtomicLongConstructor;
import net.vurs.conn.ConnectionPool;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.operation.predicate.SQLFieldPredicate;
import net.vurs.entity.Entity;
import net.vurs.entity.OpenEntity;
import net.vurs.entity.definition.analysis.PhraseInference;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.entity.definition.concept.ConceptLink;
import net.vurs.entity.definition.concept.analysis.ConceptToken;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.concept.ConceptFarmer;
import net.vurs.service.definition.concept.source.DMOZConceptSource;
import net.vurs.service.definition.entity.manager.concept.ConceptLinkManager;
import net.vurs.service.definition.entity.manager.concept.ConceptManager;
import net.vurs.service.definition.entity.manager.concept.analysis.ConceptTokenManager;
import net.vurs.service.definition.nlp.TokenModel;
import net.vurs.service.definition.nlp.WiktionaryDictionary;
import net.vurs.service.definition.nlp.token.TokenGrid;
import net.vurs.service.definition.nlp.token.TokenPhrase;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.service.event.EventHandler;
import net.vurs.util.ErrorControl;
import net.vurs.util.EscapeUtil;

@SuppressWarnings("unused")
public class ConceptService extends Service {
	
	private TransactionService transactionService = null;
	private EntityService entityService = null;
	private ConnectionService connectionService = null;
	private ClusterService clusterService = null;
	private ExternalService externalService = null;
	private NLPService nlpService = null;
	
	private ConceptManager conceptManager = null;
	private ConceptLinkManager conceptLinkManager = null;
	private ConceptTokenManager conceptTokenManager = null;
	private ConnectionPool<SQLConnection> sqlPool = null;
	
	private DMOZConceptSource DMOZSource = null;
	
	private ConceptFarmer conceptFarmer = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.transactionService = serviceManager.getService(TransactionService.class);
		this.entityService = serviceManager.getService(EntityService.class);
		this.connectionService = serviceManager.getService(ConnectionService.class);
		this.clusterService = serviceManager.getService(ClusterService.class);
		this.externalService = serviceManager.getService(ExternalService.class);
		this.nlpService = serviceManager.getService(NLPService.class);
		
		this.conceptManager = this.entityService.getManager(Concept.class, ConceptManager.class);
		this.conceptLinkManager = this.entityService.getManager(ConceptLink.class, ConceptLinkManager.class);
		this.conceptTokenManager = this.entityService.getManager(ConceptToken.class, ConceptTokenManager.class);
		this.sqlPool = this.connectionService.getPool(SQLConnection.class);
		
		this.DMOZSource = new DMOZConceptSource(this.conceptManager, this.conceptLinkManager);
		this.DMOZSource.init();
		
		this.conceptFarmer = new ConceptFarmer(this.clusterService, this.transactionService, this.conceptManager, this.conceptLinkManager, this.conceptTokenManager, this.nlpService);
	}
	
	public List<Entity<Concept>> search(String key) {
		return this.conceptManager.query(
				new SQLFieldPredicate<Concept, String>(
						Concept.name, 
						SQLFieldPredicate.Comparator.LIKE, 
						new StringBuilder().append('%').append(key).append('%').toString()));
	}
	
	@SuppressWarnings("unchecked")
	public Ranking<Pair<Integer, Entity<Concept>>> search(Tokenization tokenization) {		
		List<Tokenization> ngrams = tokenization.ngrams(1, 2, 3);
		
		List<TokenPhrase> tokenPhrases = new ArrayList<TokenPhrase>();
		List<String> tokenPhraseStrings = new ArrayList<String>();
		
		AtomicLong tokenizationCountTotal = new AtomicLong(0);
		Map<String, AtomicLong> tokenizationCounts = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		Map<String, AtomicLong> tokenConceptCounts = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		
		for (Tokenization gram: ngrams) {
			TokenGrid grid = this.nlpService.getTokenGrid(gram);
			tokenPhrases.addAll(grid.getPhrases());
		}
		
		for (TokenPhrase phrase: tokenPhrases) tokenPhraseStrings.add(phrase.getPhrase());
		Map<String, OpenEntity<ConceptToken>> inferenceMap = this.conceptTokenManager.getOpen(tokenPhraseStrings);
				
		Set<String> conceptKeySet = new HashSet<String>();
		List<String> conceptKeys = new ArrayList<String>();
		
		for (TokenPhrase tokenPhrase: tokenPhrases) {
			String phraseText = tokenPhrase.getPhrase();
			
			OpenEntity<ConceptToken> inference = inferenceMap.get(phraseText);
			if (inference == null) continue;
			
			for (Entry<String, Object> e: inference.getNode().getMap().entrySet()) {
				List<String> l = (List<String>) e.getValue();
				Integer phraseActivityCount = l.size();
				tokenizationCountTotal.addAndGet(phraseActivityCount);
				tokenizationCounts.get(e.getKey()).addAndGet(phraseActivityCount);
				tokenConceptCounts.get(e.getKey()).incrementAndGet();
				
				if (conceptKeySet.add(e.getKey())) {
					conceptKeys.add(e.getKey());
				}
			}
		}
		
		Map<String, Entity<Concept>> conceptMap = this.conceptManager.get(conceptKeys);
		
		Float endCountTotal = tokenizationCountTotal.floatValue();
		Ranking<Pair<Integer, Entity<Concept>>> conceptFrequencyRank = new Ranking<Pair<Integer, Entity<Concept>>>();
		
		for (Entry<String, AtomicLong> count: tokenizationCounts.entrySet()) {
			String conceptKey = count.getKey();
			Entity<Concept> concept = conceptMap.get(conceptKey);
			Integer conceptPhraseMentions = tokenConceptCounts.get(conceptKey).intValue();
			Float activityFrequency = count.getValue().floatValue();
			conceptFrequencyRank.add(activityFrequency / endCountTotal, new Pair<Integer, Entity<Concept>>(conceptPhraseMentions, concept));
		}
		
		return conceptFrequencyRank;
	}
	
	public void startFarmer() { this.conceptFarmer.start(); }
	public void stopFarmer() { this.conceptFarmer.stop(); }
	
}
