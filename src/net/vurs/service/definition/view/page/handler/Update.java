package net.vurs.service.definition.view.page.handler;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.common.MultiMap;
import net.vurs.common.Pair;
import net.vurs.common.Ranking;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.PingData;
import net.vurs.entity.definition.User;
import net.vurs.entity.definition.UserData;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.service.definition.logic.controller.ActivityLogic;
import net.vurs.service.definition.logic.controller.PingLogic;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.service.definition.view.page.PageHandler;
import net.vurs.service.definition.view.page.PageRequest;

public class Update extends PageHandler {

	private PingLogic pingLogic = null;
	private ActivityLogic activityLogic = null;
	
	@Override
	public void init() {
		pingLogic = this.logicService.get(PingLogic.class);
		activityLogic = this.logicService.get(ActivityLogic.class);	
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/update/ping"})
	public void ping(PageRequest request) {
		String ping = request.getParameter("ping");		
		
		TokenAnalysis analysis = this.pingLogic.process(ping);
		Ranking<Entity<ActivityDefinition>> activities = activityLogic.search(ping);
		
		request.addRenderVar("ping", ping);
		request.addRenderVar("analysis", analysis);
		request.addRenderVar("activities", activities);
		request.renderTemplate("update/ping.html");
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/update/activity/search"})
	public void activitySearch(PageRequest request) {		
		String search = request.getParameter("search");
		
		List<Entity<Concept>> concepts = this.activityLogic.findConcepts(search);
		Entity<Concept> top = concepts.get(0);
		
		List<Entity<ActivityDefinition>> existingTop = this.activityLogic.search(top.get(Concept.name)).ranked();
		Entity<ActivityDefinition> topActivity = null;
		if (!existingTop.isEmpty()) {
			topActivity = existingTop.get(0);
		} else {
			topActivity = activityLogic.create(top.get(Concept.name), top);
		}
		
		Ranking<Pair<Integer, Entity<Concept>>> rankedConcepts = this.activityLogic.findRankedConcepts(search);
		Ranking<Entity<ActivityDefinition>> searchedActivities = this.activityLogic.search(search);
		
		request.addRenderVar("top", topActivity);
		request.addRenderVar("ranked", rankedConcepts);
		request.addRenderVar("searched", searchedActivities);
		request.renderTemplate("update/activity/search.html");
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.USER)
	@Routing(patterns={"/update/add"})
	public void add(PageRequest request) {
		String phrase = request.removeParameter("phrase");
		String activityKey = request.removeParameter("activity");
		String lat = request.removeParameter("lat");
		String lon = request.removeParameter("lon");
		
		Entity<ActivityDefinition> activity = this.activityLogic.get(activityKey);
		
		if (activity != null) {
			Entity<User> user = request.getUser();
			
			Pair<Double, Double> location = null;
			if (lat == null || lon == null || lat.length() == 0 || lon.length() == 0) {
				location = this.getLocation(request);
			} else {
				location = new Pair<Double, Double>(Double.valueOf(lat), Double.valueOf(lon));
			}
			
			MultiMap<String, String> relationships = request.getParameterMap();
			
			Entity<Ping> ping = pingLogic.create(user, activity, phrase, location.a(), location.b(), null, relationships);
			List<Entity<BadgeEarned>> badges = pingLogic.processNeighbourhoods(ping).getBadges();
			Pair<Entity<UserData>, Entity<PingData>> data =  pingLogic.getData(ping);
			
			request.addRenderVar("ping", ping);
			request.addRenderVar("badges", badges);
			request.addRenderVar("userData", data.a());
			request.addRenderVar("pingData", data.b());
			request.renderTemplate("update/add.html");
		} else {
			request.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
}
