package net.vurs.service.definition.nlp.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.vurs.common.Pair;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.service.definition.entity.manager.activity.ActivityDefinitionManager;

public class TokenAnalysis {	
	public static final int DEFAULT_COUNT = 5;
	public static final float DEFAULT_FREQUENCY = 0.5f;
	
	private Tokenization tokenization = null;
	private List<TokenPhrase> phrases = null;
	private List<Pair<Integer, String>> activityRank = null;
	private Map<String, List<Pair<Integer, String>>> interpolationRanks = null;
	private ActivityDefinitionManager activityDefinitionManager = null;
	
	public TokenAnalysis(Tokenization tokenization, List<TokenPhrase> phrases, List<Pair<Integer, String>> activityRank, Map<String, List<Pair<Integer, String>>> interpolationRanks, ActivityDefinitionManager activityDefinitionManager) {
		this.tokenization = tokenization;
		this.phrases = phrases;
		this.activityRank = activityRank;
		this.interpolationRanks = interpolationRanks;
		this.activityDefinitionManager = activityDefinitionManager;
	}
	
	public Map<String, List<Pair<Integer, String>>> getInterpolations() {
		return this.interpolationRanks;
	}
	
	public List<Pair<String, List<Pair<Integer, String>>>> interpolationList() {
		List<Pair<String, List<Pair<Integer, String>>>> ret = new ArrayList<Pair<String, List<Pair<Integer, String>>>>();
		
		for (Entry<String, List<Pair<Integer, String>>> e: this.interpolationRanks.entrySet()) {
			ret.add(new Pair<String, List<Pair<Integer, String>>>(e.getKey(), e.getValue()));
		}
		
		return ret;
	}
	
	public List<Entity<ActivityDefinition>> proxies() {
		List<Entity<ActivityDefinition>> ret = new ArrayList<Entity<ActivityDefinition>>();
		
		for (Pair<Integer, String> pair: activityRank) {
			ret.add(activityDefinitionManager.getProxy(pair.b()));
		}
		
		return ret;
	}
	
	public List<Entity<ActivityDefinition>> top() { return this.top(DEFAULT_COUNT); }
	public List<Entity<ActivityDefinition>> top(int count) {
		List<Entity<ActivityDefinition>> ret = new ArrayList<Entity<ActivityDefinition>>(count);
		
		if (count > activityRank.size()) count = activityRank.size();
		
		for (int i = 0; i < count; i++) {
			ret.add(activityDefinitionManager.getProxy(this.activityRank.get(i).b()));
		}
		
		activityDefinitionManager.materialize(ret);
		
		return ret;
	}
	
	public List<Entity<ActivityDefinition>> relevant() { return this.relevant(DEFAULT_COUNT, DEFAULT_FREQUENCY); }
	public List<Entity<ActivityDefinition>> relevant(int count) { return this.relevant(count, DEFAULT_FREQUENCY); }
	public List<Entity<ActivityDefinition>> relevant(float frequency) { return this.relevant(DEFAULT_COUNT, frequency); }
	public List<Entity<ActivityDefinition>> relevant(int count, float frequency) {
		List<Entity<ActivityDefinition>> ret = new ArrayList<Entity<ActivityDefinition>>(count);
		
		if (count > activityRank.size()) count = activityRank.size();
		
		Integer requiredOccurance = ((Float) (this.phrases.size() * frequency)).intValue();

		for (int i = 0; i < count; i++) {
			Pair<Integer, String> pair = this.activityRank.get(i);
			if (pair.a() < requiredOccurance) break;
			ret.add(activityDefinitionManager.getProxy(pair.b()));
		}
		
		activityDefinitionManager.materialize(ret);
		
		return ret;
	}
	
	public List<Pair<Float, Entity<ActivityDefinition>>> scored() { return this.scored(DEFAULT_COUNT, DEFAULT_FREQUENCY); }
	public List<Pair<Float, Entity<ActivityDefinition>>> scored(int count) { return this.scored(count, DEFAULT_FREQUENCY); }
	public List<Pair<Float, Entity<ActivityDefinition>>> scored(float frequency) { return this.scored(DEFAULT_COUNT, frequency); }
	public List<Pair<Float, Entity<ActivityDefinition>>> scored(int count, float frequency) {		
		List<Pair<Float, Entity<ActivityDefinition>>> ret = new ArrayList<Pair<Float, Entity<ActivityDefinition>>>(count);
		List<Entity<ActivityDefinition>> definitions = new ArrayList<Entity<ActivityDefinition>>(count);
		
		if (count > activityRank.size()) count = activityRank.size();
		
		Integer phraseCount = this.phrases.size();

		for (int i = 0; i < count; i++) {
			Pair<Integer, String> pair = this.activityRank.get(i);
			
			float occurance = pair.a().floatValue() / phraseCount.floatValue();
			if (occurance < frequency) break;
			
			Entity<ActivityDefinition> proxy = activityDefinitionManager.getProxy(pair.b());
			ret.add(new Pair<Float, Entity<ActivityDefinition>>(occurance, proxy));
			definitions.add(proxy);
		}
		
		activityDefinitionManager.materialize(definitions);
		
		return ret;
	}
	
	@Override
	public String toString() {
		return String.format("Analysis result for '%s': %s", this.tokenization.text(), this.top());
	}
	
}