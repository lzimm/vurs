package net.vurs.service.definition.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.ConstructingHashMap;
import net.vurs.common.Pair;
import net.vurs.common.Ranking;
import net.vurs.common.constructor.AtomicLongConstructor;
import net.vurs.common.constructor.AtomicLongMapConstructor;
import net.vurs.entity.Entity;
import net.vurs.entity.OpenEntity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.PingInterpolation;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.analysis.PhraseInference;
import net.vurs.entity.definition.analysis.PhraseInterpolation;
import net.vurs.entity.definition.analysis.PhraseReference;
import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.entity.manager.activity.ActivityDefinitionManager;
import net.vurs.service.definition.entity.manager.analysis.PhraseInferenceManager;
import net.vurs.service.definition.entity.manager.analysis.PhraseInterpolationManager;
import net.vurs.service.definition.entity.manager.analysis.PhraseReferenceManager;
import net.vurs.service.definition.nlp.regex.RegexTokenizer;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.service.definition.nlp.token.TokenGrid;
import net.vurs.service.definition.nlp.token.TokenInterpolation;
import net.vurs.service.definition.nlp.token.TokenPhrase;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.util.SimpleProfiler;

public class TokenModel {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	private WiktionaryDictionary dictionary = null;
	private TweetModel tweetModel = null;
	private RegexTokenizer tokenizer = null;
	
	private PhraseInferenceManager phraseInferenceManager = null;
	private PhraseInterpolationManager phraseInterpolationManager = null;
	private PhraseReferenceManager phraseReferenceManager = null;
	private ActivityDefinitionManager activityDefinitionManager = null;
	
	public TokenModel(EntityService entityService, WiktionaryDictionary dictionary, TweetModel tweetModel) {
		this.logger.info("Loading token modelling framework");
		
		this.entityService = entityService;
		this.tokenizer = new RegexTokenizer();
		this.phraseInferenceManager = this.entityService.getManager(PhraseInference.class, PhraseInferenceManager.class);
		this.phraseInterpolationManager = this.entityService.getManager(PhraseInterpolation.class, PhraseInterpolationManager.class);
		this.phraseReferenceManager = this.entityService.getManager(PhraseReference.class, PhraseReferenceManager.class);
		this.activityDefinitionManager = this.entityService.getManager(ActivityDefinition.class, ActivityDefinitionManager.class);
		this.dictionary = dictionary;
		this.tweetModel = tweetModel;
	}
	
	public Tokenization tokenize(String line) {
		return this.tokenizer.tokenize(line);
	}
		
	@SuppressWarnings("unchecked")
	public TokenAnalysis analyze(Tokenization tokenization) {
		SimpleProfiler profiler = new SimpleProfiler(logger, "analyze");
		
		List<Tokenization> ngrams = tokenization.ngrams(1, 2, 3);
		
		profiler.profile("ngrams");
		
		AtomicLong tokenizationCountTotal = new AtomicLong(0);
		Map<String, AtomicLong> tokenizationCounts = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		Map<String, AtomicLong> tokenActivityCounts = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		
		Map<String, AtomicLong> interpolationCountTotals = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		Map<String, Map<String, AtomicLong>> interpolationCounts = new ConstructingHashMap<String, Map<String, AtomicLong>>(new AtomicLongMapConstructor<String, String>());
		Map<String, Map<String, AtomicLong>> interpolatedValueCounts = new ConstructingHashMap<String, Map<String, AtomicLong>>(new AtomicLongMapConstructor<String, String>());
		
		List<String> tokenPhraseStrings = new ArrayList<String>();
		List<TokenPhrase> tokenPhrases = new ArrayList<TokenPhrase>();
		
		List<String> tokenInterpolationStrings = new ArrayList<String>();
		List<TokenInterpolation> tokenInterpolations = new ArrayList<TokenInterpolation>();
		
		// first assemble the token grid (which contains a bunch of verb:modifier pairings
		// then add the phrases from the grid into the phrase and interpolation lists
		
		for (Tokenization gram: ngrams) {
			TokenGrid grid = this.getGrid(gram);
			
			tokenPhrases.addAll(grid.getPhrases());
			tokenInterpolations.addAll(grid.getInterpolationCandidates());
		}
		
		logger.warn(String.format("phrases %s, interpolations %s", tokenPhrases.size(), tokenInterpolations.size()));
		
		profiler.profile("grid reduction");
		
		// traverse the phrases, looking up any existing mappings that already exist
		// then contribute to global activity counts for the entire tokenization
		
		for (TokenPhrase phrase: tokenPhrases) tokenPhraseStrings.add(phrase.getPhrase());
		Map<String, OpenEntity<PhraseInference>> inferenceMap = this.phraseInferenceManager.getOpen(tokenPhraseStrings);
		
		profiler.profile("fetching inferences");
		
		for (TokenPhrase tokenPhrase: tokenPhrases) {
			String phraseText = tokenPhrase.getPhrase();
			
			OpenEntity<PhraseInference> inference = inferenceMap.get(phraseText);
			if (inference == null) continue;
			
			for (Entry<String, Object> e: inference.getNode().getMap().entrySet()) {
				List<String> l = (List<String>) e.getValue();
				Integer phraseActivityCount = l.size();
				tokenizationCountTotal.addAndGet(phraseActivityCount);
				tokenizationCounts.get(e.getKey()).addAndGet(phraseActivityCount);
				tokenActivityCounts.get(e.getKey()).incrementAndGet();
			}
		}
		
		profiler.profile("processing inferences");
		
		for (TokenInterpolation interpolation: tokenInterpolations) tokenInterpolationStrings.add(interpolation.getPhrase());
		Map<String, OpenEntity<PhraseInterpolation>> interpolationMap = this.phraseInterpolationManager.getOpen(tokenInterpolationStrings);
		
		profiler.profile("fetching interpolations");
		
		for (TokenInterpolation tokenInterpolation: tokenInterpolations) {
			String interpolationText = tokenInterpolation.getPhrase();
			
			OpenEntity<PhraseInterpolation> interpolation = interpolationMap.get(interpolationText);
			if (interpolation == null) continue;
			
			for (Entry<String, Object> e: interpolation.getNode().getMap().entrySet()) {
				List<String> l = (List<String>) e.getValue();
				Integer phraseInterpolationCount = l.size();
				interpolationCountTotals.get(tokenInterpolation.getValue()).addAndGet(phraseInterpolationCount);
				interpolationCounts.get(tokenInterpolation.getValue()).get(e.getKey()).addAndGet(phraseInterpolationCount);
				interpolatedValueCounts.get(tokenInterpolation.getValue()).get(e.getKey()).incrementAndGet();
			}
		}
		
		profiler.profile("processing interpolations");
		
		// normalize the counts into frequencies and rank them
		// from most frequent to least frequent
		
		Float endCountTotal = tokenizationCountTotal.floatValue();
		Ranking<Pair<Integer, String>> activityFrequencyRank = new Ranking<Pair<Integer, String>>();
		
		for (Entry<String, AtomicLong> count: tokenizationCounts.entrySet()) {
			String activityKey = count.getKey();
			Integer activityPhraseMentions = tokenActivityCounts.get(activityKey).intValue();
			Float activityFrequency = count.getValue().floatValue();
			activityFrequencyRank.add(activityFrequency / endCountTotal, new Pair<Integer, String>(activityPhraseMentions, activityKey));
		}
		
		Map<String, List<Pair<Integer, String>>> interpolationRanks = new HashMap<String, List<Pair<Integer, String>>>();
		for (Entry<String, AtomicLong> interpolationCountTotal: interpolationCountTotals.entrySet()) {
			String interpolatedValue = interpolationCountTotal.getKey();
			Float interpolatedCountTotal = interpolationCountTotal.getValue().floatValue();
			Ranking<Pair<Integer, String>> interpolationFrequencyRank = new Ranking<Pair<Integer, String>>();
			
			for (Entry<String, AtomicLong> count: interpolationCounts.get(interpolatedValue).entrySet()) {
				String relationshipKey = count.getKey();
				Integer interpolationOccurances = interpolatedValueCounts.get(interpolatedValue).get(relationshipKey).intValue();
				Float interpolationFrequency = count.getValue().floatValue();
				interpolationFrequencyRank.add(interpolationFrequency / interpolatedCountTotal, new Pair<Integer, String>(interpolationOccurances, relationshipKey));
			}		
			
			interpolationRanks.put(interpolatedValue, interpolationFrequencyRank.ranked());
		}
		
		profiler.profile("normalizing results");
		
		return new TokenAnalysis(tokenization, tokenPhrases, activityFrequencyRank.ranked(), interpolationRanks, this.activityDefinitionManager);
	}
	
	public void train(Tokenization tokenization, Entity<Ping> ping, List<Entity<PingInterpolation>> interpolations) {
		List<Tokenization> ngrams = tokenization.ngrams(1, 2, 3);
		for (Tokenization gram: ngrams) {			
			TokenGrid grid = getGrid(gram);
			
			List<TokenPhrase> phrases = grid.getPhrases();			
			if (phrases.size() > 0) {
				for (TokenPhrase phrase: phrases) {
					String phraseText = phrase.getPhrase();

					this.phraseInferenceManager.train(phraseText, ping);
					this.phraseReferenceManager.train(ping.get(Ping.activity), phraseText, ping);
				}
			}
			
			for (Entity<PingInterpolation> interpolation: interpolations) {
				List<TokenInterpolation> interpolated = grid.getInterpolations(interpolation.get(PingInterpolation.value));				
				if (interpolated.size() > 0) {
					for (TokenInterpolation tokens: interpolated) {
						String interpolationText = tokens.getPhrase();
						
						this.phraseInterpolationManager.train(interpolationText, interpolation, ping);
					}
				}
			}
		}
	}
	
	public TokenGrid getGrid(Tokenization tokenization) {
		TokenGrid grid = new TokenGrid(this.tweetModel);
		
		for (String token: tokenization.tokens()) {
			grid.add(token, this.dictionary.define(token));
		}
		
		return grid;
	}

}
