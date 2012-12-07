package net.vurs.service.definition.logic.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;

import net.vurs.common.MultiMap;
import net.vurs.common.Pair;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.PingData;
import net.vurs.entity.definition.PingInterpolation;
import net.vurs.entity.definition.User;
import net.vurs.entity.definition.UserData;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.activity.ActivityPinged;
import net.vurs.entity.definition.location.Location;
import net.vurs.service.definition.entity.manager.PingDataManager;
import net.vurs.service.definition.entity.manager.PingInterpolationManager;
import net.vurs.service.definition.entity.manager.PingManager;
import net.vurs.service.definition.entity.manager.UserDataManager;
import net.vurs.service.definition.entity.manager.activity.ActivityDefinitionManager;
import net.vurs.service.definition.entity.manager.activity.ActivityPingedManager;
import net.vurs.service.definition.logic.LogicController;
import net.vurs.service.definition.neighbourhood.PingResult;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.util.SimpleProfiler;

public class PingLogic extends LogicController {

	private PingManager pingManager = null;
	private PingDataManager pingDataManager = null;
	private PingInterpolationManager pingInterpolationManager = null;
	private ActivityDefinitionManager activityDefinitionManager = null;
	private ActivityPingedManager activityPingManager = null;
	private UserDataManager userDataManager = null;
	
	private Freebase freebase;

	@Override
	public void init() {
		this.pingManager = this.entityService.getManager(Ping.class, PingManager.class);
		this.pingDataManager = this.entityService.getManager(PingData.class, PingDataManager.class);
		this.pingInterpolationManager = this.entityService.getManager(PingInterpolation.class, PingInterpolationManager.class);
		this.activityDefinitionManager  = this.entityService.getManager(ActivityDefinition.class, ActivityDefinitionManager.class);
		this.activityPingManager  = this.entityService.getManager(ActivityPinged.class, ActivityPingedManager.class);
		this.userDataManager = this.entityService.getManager(UserData.class, UserDataManager.class);
		
		this.freebase = this.externalService.getFreebase();
	}
	
	public TokenAnalysis process(String data) {
		return this.nlpService.process(data);
	}
	
	public List<JSON> queryFreebaseTopics(String query) {
		List<JSON> res = new ArrayList<JSON>();
		
		JSON fb = this.freebase.search(query);
		
		int i = -1;
		while (fb.has(++i)) {
			JSON r = fb.get(i);
			res.add(r);
		}
		
		return res;
	}
	
	public Entity<Ping> create(Entity<User> user, Entity<ActivityDefinition> activity, String description, Double lat, Double lon, Entity<Location> location, MultiMap<String, String> relationships) {
		SimpleProfiler profiler = new SimpleProfiler(logger, "create");
		
		Entity<Ping> ping = this.pingManager.create();
		ping.set(Ping.user, user);
		ping.set(Ping.activity, activity);
		ping.set(Ping.description, description);
		ping.set(Ping.lat, lat);
		ping.set(Ping.lon, lon);
		ping.set(Ping.location, location);
		this.pingManager.save(ping);
		
		profiler.profile("ping");
		
		List<Entity<PingInterpolation>> interpolations = new ArrayList<Entity<PingInterpolation>>(relationships.size());
		for (Entry<String, List<String>> relationship: relationships.entrySet()) {
			for (String value: relationship.getValue()) {
				Entity<PingInterpolation> interpolation = this.pingInterpolationManager.create();
				interpolation.set(PingInterpolation.ping, ping);
				interpolation.set(PingInterpolation.relationship, relationship.getKey());
				interpolation.set(PingInterpolation.value, value);
				
				this.pingInterpolationManager.save(interpolation);
				this.pingDataManager.insert(ping.getKey(), PingData.interpolations, interpolation);
				
				interpolations.add(interpolation);
			}
		}
		
		profiler.profile("interpolations");
		
		this.userDataManager.insert(user.getKey(), UserData.pings, ping);
		
		Entity<ActivityPinged> activityPing = this.activityPingManager.create();
		activityPing.set(ActivityPinged.ping, ping);
		activityPing.set(ActivityPinged.user, user);
		activityPing.set(ActivityPinged.definition, activity);
		this.activityPingManager.save(activityPing);
		
		profiler.profile("indexing");
		
		this.nlpService.train(description, ping, interpolations);
		
		profiler.profile("training");
		
		return ping;
	}

	public Pair<Entity<UserData>, Entity<PingData>> getData(Entity<Ping> ping) {
		Entity<UserData> userData = this.userDataManager.get(ping.get(Ping.user).getKey());
		Entity<PingData> pingData = this.pingDataManager.get(ping.getKey());

		List<Entity<Ping>> pings = userData.get(UserData.pings);
		this.pingManager.materialize(pings);
		
		List<Entity<ActivityDefinition>> activities = new ArrayList<Entity<ActivityDefinition>>(pings.size());
		for (Entity<Ping> p: pings) { activities.add(p.get(Ping.activity)); }		
		this.activityDefinitionManager.materialize(activities);
		
		return new Pair<Entity<UserData>, Entity<PingData>>(userData, pingData);
	}

	public PingResult processNeighbourhoods(Entity<Ping> ping) {
		return this.neighbourhoodService.process(ping);
	}

}
