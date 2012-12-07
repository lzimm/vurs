package net.vurs.service.definition.view.api.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.common.CountingRanker;
import net.vurs.common.MultiMap;
import net.vurs.common.Pair;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.User;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.service.definition.logic.controller.ActivityLogic;
import net.vurs.service.definition.logic.controller.PingLogic;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.service.definition.view.api.APIHandler;
import net.vurs.service.definition.view.api.APIRequest;

public class Update extends APIHandler {

	private PingLogic pingLogic = null;
	private ActivityLogic activityLogic = null;
	
	@Override
	public void init() {
		pingLogic = this.logicService.get(PingLogic.class);
		activityLogic = this.logicService.get(ActivityLogic.class);	
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/update/ping"})
	public void ping(APIRequest request) {
		String ping = request.getParameter("ping");		
		
		TokenAnalysis analysis = this.pingLogic.process(ping);
		
		List<Pair<Float, Entity<ActivityDefinition>>> processed = analysis.scored();
		Map<String, Map<String, String>> processedMap = new HashMap<String, Map<String, String>>();
		for (Pair<Float, Entity<ActivityDefinition>> pair: processed) {
			Map<String, String> activitySummary = new HashMap<String, String>();
			
			Entity<ActivityDefinition> activity = pair.b();
			activitySummary.put("score", pair.a().toString());
			activitySummary.put("name", activity.get(ActivityDefinition.name));
			activitySummary.put("concept", activity.get(ActivityDefinition.concept).get(Concept.name));
			
			processedMap.put(activity.getKey(), activitySummary);
		}
		
		Map<String, List<Pair<Integer, String>>> interpolations = analysis.getInterpolations();
		
		List<Entity<ActivityDefinition>> queried = activityLogic.search(ping).ranked();
		Map<String, Map<String, String>> queriedMap = new HashMap<String, Map<String, String>>();
		for (Entity<ActivityDefinition> activity: queried) {
			Map<String, String> activitySummary = new HashMap<String, String>();

			activitySummary.put("name", activity.get(ActivityDefinition.name));
			activitySummary.put("concept", activity.get(ActivityDefinition.concept).get(Concept.name));
			
			queriedMap.put(activity.getKey(), activitySummary);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("processed", processedMap);
		result.put("interpolations", interpolations);
		result.put("queried", queriedMap);
		
		request.sendResponse(result);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/update/activity/search"})
	public void activitySearch(APIRequest request) {		
		String search = request.getParameter("search");
		
		List<Entity<Concept>> concepts = this.activityLogic.findConcepts(search);
		List<Pair<Integer, Entity<Concept>>> rankedConcepts = this.activityLogic.findRankedConcepts(search).ranked();
		
		List<String> conceptKeys = new ArrayList<String>(concepts.size() + rankedConcepts.size());
		
		Map<String, Entity<Concept>> conceptMap = new HashMap<String, Entity<Concept>>();
		for (Entity<Concept> concept: concepts) {
			String key = concept.get(Concept.name);
			
			conceptMap.put(key, concept);
			conceptKeys.add(key);
		}
		
		Map<String, Map<String, String>> rankedSummaries = new HashMap<String, Map<String, String>>();
		for (Pair<Integer, Entity<Concept>> r: rankedConcepts) {
			String key = r.b().get(Concept.name);
			
			Map<String, String> rankedSummary = new HashMap<String, String>();
			rankedSummary.put("score", r.a().toString());
			rankedSummary.put("name", r.b().get(Concept.name));
			rankedSummary.put("concept", r.b().get(Concept.path));
			rankedSummaries.put(key, rankedSummary);
			
			conceptMap.put(key, r.b());
			conceptKeys.add(key);
		}
		
		Map<String, Map<String, String>> rankedMap = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> queriedMap = new HashMap<String, Map<String, String>>();
		
		Map<String, CountingRanker<Entity<ActivityDefinition>>> existing = this.activityLogic.search(conceptKeys); 
		for (String key: conceptKeys) {
			List<Entity<ActivityDefinition>> existingRanked = existing.get(key).ranking().ranked();
			Entity<ActivityDefinition> conceptTop = null;
			if (!existingRanked.isEmpty()) {
				conceptTop = existingRanked.get(0);
			} else {
				conceptTop = activityLogic.create(key, conceptMap.get(key));
			}
			
			Map<String, String> ranked = rankedSummaries.remove(key);
			if (ranked != null) {
				rankedMap.put(conceptTop.getKey(), ranked);
			} else {
				Map<String, String> activitySummary = new HashMap<String, String>();
				activitySummary.put("name", conceptTop.get(ActivityDefinition.name));
				activitySummary.put("concept", conceptTop.get(ActivityDefinition.concept).get(Concept.path));
				queriedMap.put(conceptTop.getKey(), activitySummary);
			}
		}
		
		Map<String, Map<String, String>> trainedMap = new HashMap<String, Map<String, String>>();
		
		List<Entity<ActivityDefinition>> trained = this.activityLogic.search(search).ranked();
		for (Entity<ActivityDefinition> q: trained) {
			Map<String, String> trainedSummary = new HashMap<String, String>();
			trainedSummary.put("name", q.get(ActivityDefinition.name));
			trainedSummary.put("concept", q.get(ActivityDefinition.concept).get(Concept.path));
			trainedMap.put(q.getKey(), trainedSummary);
		}
		
		Map<String, Map<String, Map<String, String>>> resultMap = new HashMap<String, Map<String, Map<String, String>>>();
		resultMap.put("trained", trainedMap);
		resultMap.put("queried", queriedMap);
		resultMap.put("ranked", rankedMap);
		
		request.sendResponse(resultMap);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.USER)
	@Routing(patterns={"/update/add"})
	public void add(APIRequest request) {
		String phrase = request.removeParameter("phrase");
		String activityKey = request.removeParameter("activity");
		String lat = request.removeParameter("lat");
		String lon = request.removeParameter("lon");
		
		Entity<ActivityDefinition> activity = this.activityLogic.get(activityKey);
		
		if (activity != null) {
			Entity<User> user = request.getUser();
			
			Pair<Double, Double> location = null;
			if (lat == null && lon == null) {
				location = this.getLocation(request);
			} else {
				location = new Pair<Double, Double>(Double.valueOf(lat), Double.valueOf(lon));
			}
			
			MultiMap<String, String> relationships = request.getParameterMap();
			
			Entity<Ping> ping = pingLogic.create(user, activity, phrase, location.a(), location.b(), null, relationships);
			List<Entity<BadgeEarned>> badges = pingLogic.processNeighbourhoods(ping).getBadges();
			
			Map<String, String> pingSummary = new HashMap<String, String>();
			pingSummary.put("ping", ping.getKey());
			
			if (badges != null) {
				List<String> badgeKeys = new ArrayList<String>(badges.size());
				for (Entity<BadgeEarned> badge: badges) {
					badgeKeys.add(badge.getKey());
				}
				pingSummary.put("badges", badgeKeys.toString());
			}
			
			request.sendResponse(pingSummary);
		} else {
			request.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
}
