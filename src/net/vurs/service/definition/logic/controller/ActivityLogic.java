package net.vurs.service.definition.logic.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.vurs.common.CountingRanker;
import net.vurs.common.MultiMap;
import net.vurs.common.Pair;
import net.vurs.common.Ranking;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.activity.ActivityHierarchy;
import net.vurs.entity.definition.activity.ActivityIndex;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.service.definition.entity.manager.activity.ActivityDefinitionManager;
import net.vurs.service.definition.entity.manager.activity.ActivityHierarchyManager;
import net.vurs.service.definition.entity.manager.activity.ActivityIndexManager;
import net.vurs.service.definition.logic.LogicController;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.util.ErrorControl;

public class ActivityLogic extends LogicController {

	private ActivityDefinitionManager definitionManager = null;
	private ActivityHierarchyManager hierarchyManager = null;
	private ActivityIndexManager indexManager = null;

	@Override
	public void init() {
		this.definitionManager = this.entityService.getManager(ActivityDefinition.class, ActivityDefinitionManager.class);
		this.hierarchyManager = this.entityService.getManager(ActivityHierarchy.class, ActivityHierarchyManager.class);
		this.indexManager = this.entityService.getManager(ActivityIndex.class, ActivityIndexManager.class);
	}
	
	public Entity<ActivityDefinition> get(String key) { 
		return this.definitionManager.get(key);
	}
	
	public Map<String, CountingRanker<Entity<ActivityDefinition>>> search(List<String> query) {
		Map<String, CountingRanker<Entity<ActivityDefinition>>> rankers = new HashMap<String, CountingRanker<Entity<ActivityDefinition>>>(query.size());		
		
		List<String> tokens = new ArrayList<String>();
		MultiMap<String, String> inverseTokens = new MultiMap<String, String>();
		for (String key: query) {
			rankers.put(key, new CountingRanker<Entity<ActivityDefinition>>());
			
			Tokenization tokenization = this.nlpService.tokenize(key);
			for (String t: tokenization.tokens()) {
				inverseTokens.add(t, key);
				tokens.add(t);
			}
		}
		
		Map<String, Entity<ActivityIndex>> indexed = this.indexManager.get(tokens);
		
		for (Entry<String, Entity<ActivityIndex>> e: indexed.entrySet()) {
			List<Entity<ActivityDefinition>> activities = e.getValue().get(ActivityIndex.activities);
			for (Entity<ActivityDefinition> activity: activities) {
				for (String key: inverseTokens.get(e.getKey())) {
					rankers.get(key).count(activity);
				}
			}
		}
		
		return rankers;
	}
	
	public Ranking<Entity<ActivityDefinition>> search(String query) {
		return this.search(Arrays.asList(query)).get(query).ranking();
	}
	
	public List<Entity<Concept>> findConcepts(String key) {
		return this.conceptService.search(key);
	}
	
	public Ranking<Pair<Integer, Entity<Concept>>> findConcepts(Tokenization tokenization) {
		return this.conceptService.search(tokenization);
	}
	
	public Entity<ActivityDefinition> create(String name, Entity<Concept> concept) { return this.create(name, concept, null); }
	
	public Entity<ActivityDefinition> create(String name, Entity<Concept> concept, List<Entity<ActivityDefinition>> parents) {
		this.logger.info(String.format("Creating new activity: %s (%s)", name, concept.getKey()));
		
		Entity<ActivityDefinition> activity = this.definitionManager.create();
		activity.set(ActivityDefinition.name, name);
		activity.set(ActivityDefinition.concept, concept);
		this.definitionManager.save(activity);
		
		if (parents == null || parents.size() == 0) {
			parents = this.recursivelyFindParents(concept);
		}
		
		this.hierarchyManager.create(activity, parents);
		
		Tokenization tokenization = this.nlpService.tokenize(name);
		this.indexManager.index(tokenization.tokens(), activity);
		
		return activity;
	}

	private List<Entity<ActivityDefinition>> recursivelyFindParents(Entity<Concept> concept) {
		List<Entity<ActivityDefinition>> parents = new ArrayList<Entity<ActivityDefinition>>();
		
		Entity<Concept> parent = concept.get(Concept.parent);
		if (parent != null && !parent.getKey().equals(concept.getKey())) {
			try {
				parents.add(this.create(parent.get(Concept.name), parent));
				this.logger.info(String.format("Found parent %s for concept %s", parent.get(Concept.name), concept.get(Concept.name)));
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
		
		return parents;
	}

	public Ranking<Pair<Integer, Entity<Concept>>> findRankedConcepts(String search) {
		return this.findConcepts(this.nlpService.tokenize(search));
	}

}
