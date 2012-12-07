package net.vurs.service.definition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import helpers.PorterStemmer;

import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerTarget;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.PingInterpolation;
import net.vurs.service.Service;
import net.vurs.service.ServiceManager;
import net.vurs.service.definition.nlp.POSTagger;
import net.vurs.service.definition.nlp.PhraseChunker;
import net.vurs.service.definition.nlp.StringTokenizer;
import net.vurs.service.definition.nlp.TokenModel;
import net.vurs.service.definition.nlp.TweetModel;
import net.vurs.service.definition.nlp.WiktionaryDictionary;
import net.vurs.service.definition.nlp.WordNet;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.service.definition.nlp.token.TokenGrid;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;
import net.vurs.service.definition.nlp.wrappers.PhraseChunk;
import net.vurs.util.ErrorControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;

@SuppressWarnings("unused")
public class NLPService extends Service {
	private static final String WIKTIONARY_PATH = "";
	
	private EntityService entityService = null;
	private ConnectionService connectionService = null;
	
	private TweetModel tweetModel = null;
	private TokenModel tokenModel = null;
	private WordNet dictionary = null;
	private WiktionaryDictionary wiktionary = null;
	
	@Override
	public void init(ServiceManager serviceManager) {
		this.logger.info("Loading nlp service");
		
		this.entityService = serviceManager.getService(EntityService.class);
		this.connectionService = serviceManager.getService(ConnectionService.class);
		
		this.dictionary = new WordNet();
		this.tweetModel = new TweetModel();
		this.wiktionary = new WiktionaryDictionary();
		this.tokenModel = new TokenModel(this.entityService, this.wiktionary, this.tweetModel);
	}
	
	public TokenAnalysis process(String data) {
		Tokenization tokenization = this.tokenModel.tokenize(data);
		return this.tokenModel.analyze(tokenization);
	}
	
	public void train(String data, Entity<Ping> ping, List<Entity<PingInterpolation>> interpolations) {
		Tokenization tokenization = this.tokenModel.tokenize(data);
		this.tokenModel.train(tokenization, ping, interpolations);
	}

	public String findMostDistinctVerb(String data) {
		Tokenization tokenization = this.tokenModel.tokenize(data);
		
		TreeMap<Float, String> verbs = new TreeMap<Float, String>();
		for (String token: tokenization.tokens()) {
			List<TokenWordType> types = this.wiktionary.define(token);
			for (TokenWordType t: types) {
				if (t.equals(TokenWordType.VERB)) {
					verbs.put(this.tweetModel.frequency(token), token);
				}
			}
		}
		
		return verbs.pollLastEntry().getValue();
	}

	public Tokenization tokenize(String data) {
		return this.tokenModel.tokenize(data);
	}
	
	public TokenGrid getTokenGrid(Tokenization tokenization)  {
		return this.tokenModel.getGrid(tokenization);
	}

}