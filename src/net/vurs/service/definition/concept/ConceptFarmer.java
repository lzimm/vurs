package net.vurs.service.definition.concept;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.service.definition.ClusterService;
import net.vurs.service.definition.NLPService;
import net.vurs.service.definition.TransactionService;
import net.vurs.service.definition.entity.manager.concept.ConceptLinkManager;
import net.vurs.service.definition.entity.manager.concept.ConceptManager;
import net.vurs.service.definition.entity.manager.concept.analysis.ConceptTokenManager;

public class ConceptFarmer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ClusterService clusterService = null;
	private TransactionService transactionService = null;
	private ConceptManager conceptManager = null;
	private ConceptLinkManager conceptLinkManager = null;
	private ConceptTokenManager conceptTokenManager = null;
	private NLPService nlpService = null;
	
	private Thread farmerThread;
	private ConceptFarmerThread farmerRunnable;
	
	public ConceptFarmer(ClusterService clusterService, TransactionService transactionService, ConceptManager conceptManager, ConceptLinkManager conceptLinkManager, ConceptTokenManager conceptTokenManager, NLPService nlpService) {
		this.clusterService = clusterService;
		this.transactionService = transactionService;
		this.conceptManager = conceptManager;
		this.conceptLinkManager = conceptLinkManager;
		this.conceptTokenManager = conceptTokenManager;
		this.nlpService = nlpService;
	}
	
	public void start() {
		this.logger.info("Starting concept processing farmer thread");
		
		this.farmerRunnable = new ConceptFarmerThread(
				this.clusterService,
				this.transactionService,
				this.conceptManager, 
				this.conceptLinkManager, 
				this.conceptTokenManager,
				this.nlpService
			);
		
		this.farmerThread = new Thread(farmerRunnable, "ConceptFarmerThread");
		this.farmerThread.start();
	}
	
	public void stop() {
		this.farmerRunnable.stop();
	}

}
