package net.vurs.service.definition.concept;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.TimeUUID;
import net.vurs.conn.sql.SQLConnection;
import net.vurs.conn.sql.SQLNode;
import net.vurs.conn.sql.operation.params.SQLValue;
import net.vurs.conn.sql.operation.params.SQLValueList;
import net.vurs.conn.sql.operation.predicate.SQLAndPredicate;
import net.vurs.conn.sql.operation.predicate.SQLFieldPredicate;
import net.vurs.conn.sql.operation.predicate.SQLInPredicate;
import net.vurs.conn.sql.operation.predicate.SQLLimitPredicate;
import net.vurs.conn.sql.operation.predicate.SQLOrPredicate;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.concept.ConceptLink;
import net.vurs.service.definition.ClusterService;
import net.vurs.service.definition.NLPService;
import net.vurs.service.definition.TransactionService;
import net.vurs.service.definition.cluster.layer.ClusterLayer;
import net.vurs.service.definition.entity.manager.concept.ConceptLinkManager;
import net.vurs.service.definition.entity.manager.concept.ConceptManager;
import net.vurs.service.definition.entity.manager.concept.analysis.ConceptTokenManager;
import net.vurs.service.definition.nlp.TokenModel;
import net.vurs.service.definition.nlp.token.TokenGrid;
import net.vurs.service.definition.nlp.token.TokenPhrase;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;
import net.vurs.util.ErrorControl;
import net.vurs.util.SimpleProfiler;

@SuppressWarnings("unused")
public class ConceptFarmerThread implements Runnable {
	private static final int ACQUISITION_LIMIT = 10;
	private static final long PROCESSING_TIMEOUT = 1000L * 60 * 30;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ClusterService clusterService = null;
	private TransactionService transactionService = null;
	private ConceptManager conceptManager = null;
	private ConceptLinkManager conceptLinkManager = null;
	private ConceptTokenManager conceptTokenManager = null;
	private NLPService nlpService = null;
	
	private String uniqueToken = null;
	
	private boolean running = true;
	
	public ConceptFarmerThread(ClusterService clusterService, TransactionService transactionService, ConceptManager conceptManager, ConceptLinkManager conceptLinkManager, ConceptTokenManager conceptTokenManager, NLPService nlpService) {
		this.clusterService = clusterService;
		this.transactionService = transactionService;
		this.conceptManager = conceptManager;
		this.conceptLinkManager = conceptLinkManager;
		this.conceptTokenManager = conceptTokenManager;
		this.nlpService = nlpService;
		this.uniqueToken = new TimeUUID().toString();
	}

	@Override
	public void run() {
		this.logger.info("Starting concept processing farmer runner");
		
		while (this.running) {
			try {
				this.transactionService.startGlobalTransaction();
				
				List<Entity<ConceptLink>> links = acquireJobs();
				
				SimpleProfiler profiler = new SimpleProfiler(this.logger, this.conceptLinkManager.getValues(ConceptLink.link, links).toString());
				
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
				
				for (Entity<ConceptLink> link: links) {					
					processLink(link, client);
				}
				
				profiler.profile("processing");
				
				this.transactionService.getGlobalTransaction().finish();
				
				Thread.sleep(100);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}

	private List<Entity<ConceptLink>> acquireJobs() {
		List<Entity<ConceptLink>> freeLinks = this.conceptLinkManager.query(new SQLFieldPredicate<ConceptLink, String>(ConceptLink.link, SQLFieldPredicate.Comparator.NE, "")
																		.and(new SQLFieldPredicate<ConceptLink, Integer>(ConceptLink.state, ConceptLink.State.WAITING.getIndex())
																			.orAll(new SQLFieldPredicate<ConceptLink, Integer>(ConceptLink.state, ConceptLink.State.PROCESSING.getIndex()),
																					new SQLFieldPredicate<ConceptLink, Timestamp>(ConceptLink.stateTime, SQLFieldPredicate.Comparator.LT, new Timestamp(System.currentTimeMillis() - PROCESSING_TIMEOUT))))
																		.limit(ACQUISITION_LIMIT));
		
		if (freeLinks.size() > 0) {
			this.conceptLinkManager.update(new SQLValueList<ConceptLink>()
										.add(ConceptLink.state, ConceptLink.State.PROCESSING.getIndex())
										.add(ConceptLink.stateOwner, this.uniqueToken)
										.add(ConceptLink.stateTime, new Timestamp(System.currentTimeMillis())), 
									new SQLInPredicate<ConceptLink, Integer>(ConceptLink.id, freeLinks));
		}
		
		return this.conceptLinkManager.query(new SQLFieldPredicate<ConceptLink, Integer>(ConceptLink.state, ConceptLink.State.PROCESSING.getIndex())
										.and(new SQLFieldPredicate<ConceptLink, String>(ConceptLink.stateOwner, this.uniqueToken)));
	}
	
	private void processLink(Entity<ConceptLink> link, HttpClient client) {
		String href = link.get(ConceptLink.link);
		
		try {
			if (href == null || href.length() == 0) {
				link.set(ConceptLink.state, ConceptLink.State.PROCESSED.getIndex());
				this.conceptLinkManager.save(link);
				return;
			}
			
			this.logger.debug(String.format("Processing %s for concept %s", link, link.get(ConceptLink.concept).getKey()));

			try {
				HttpGet get = new HttpGet(href);
				HttpResponse r = client.execute(get);
				HttpEntity e = r.getEntity();
				
				if (r.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					link.set(ConceptLink.state, ConceptLink.State.ERROR.getIndex());
					this.conceptLinkManager.save(link);
					return;
				}
				
				HtmlCleaner cleaner = new HtmlCleaner();
				TagNode node = cleaner.clean(e.getContent()); 
				
				Tokenization tokenization = this.nlpService.tokenize(node.getText().toString());				
				List<Tokenization> ngrams = tokenization.ngrams(1, 2, 3);
				
				for (Tokenization gram: ngrams) {			
					TokenGrid grid = this.nlpService.getTokenGrid(gram);
					
					List<TokenPhrase> phrases = grid.getPhrases();
					if (phrases.size() > 0) {
						for (TokenPhrase phrase: phrases) {
							String phraseText = phrase.getPhrase();
							
							this.conceptTokenManager.train(phraseText, link);
						}
					}
				}
				
				link.set(ConceptLink.state, ConceptLink.State.PROCESSED.getIndex());
				this.conceptLinkManager.save(link);
			} catch (Exception e) {
				link.set(ConceptLink.state, ConceptLink.State.ERROR.getIndex());
				this.conceptLinkManager.save(link);
				
				this.logger.debug(String.format("Failed to process %s: %s", link, e.getMessage()));
			}

			this.logger.debug(String.format("Finished processing %s for concept %s", link, link.get(ConceptLink.concept).getKey()));
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	public void stop() {
		this.running = false;
	}
	
}
