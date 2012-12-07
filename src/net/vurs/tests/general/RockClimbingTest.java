package net.vurs.tests.general;

import java.util.List;

import net.vurs.common.MultiMap;
import net.vurs.common.Pair;
import net.vurs.common.TimeUUID;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.User;
import net.vurs.entity.definition.activity.ActivityDefinition;
import net.vurs.entity.definition.concept.Concept;
import net.vurs.service.definition.logic.controller.ActivityLogic;
import net.vurs.service.definition.logic.controller.PingLogic;
import net.vurs.service.definition.logic.controller.UserLogic;
import net.vurs.service.definition.nlp.token.TokenAnalysis;
import net.vurs.tests.TestRunner;

public class RockClimbingTest extends TestRunner {

	@SuppressWarnings("serial")
	public void test() {
		UserLogic userLogic = this.logicService.get(UserLogic.class);		
		PingLogic pingLogic = this.logicService.get(PingLogic.class);
		ActivityLogic activityLogic = this.logicService.get(ActivityLogic.class);
		
		Entity<User> user = userLogic.get("lzimm");
		if (user == null) {
			user = userLogic.create("lzimm", "lewis@lzimm.com");
			this.logger.info(String.format("0) created new user: %s", user.getKey()));
		} else {
			this.logger.info(String.format("0) using existing user: %s", user.getKey()));
		}
		
		String phrase = String.format("just climbed a mountain with @saeidm at %s", new TimeUUID());
		String intention = "climbing";
		
		this.logger.info(String.format("1) doing first proccess pass of: %s", phrase));
		
		TokenAnalysis analysis = pingLogic.process(phrase);
		
		List<Pair<Float, Entity<ActivityDefinition>>> processed = analysis.scored();
		for (Pair<Float, Entity<ActivityDefinition>> activity: processed) {
			this.logger.info(String.format("(%s) %s: %s", activity.a(), activity.b().get(ActivityDefinition.name), activity.b().get(ActivityDefinition.concept).get(Concept.path)));
		}
		
		Entity<ActivityDefinition> activity = processed.isEmpty() ? null : processed.get(0).b();
		
		this.logger.info(String.format("2) doing activity search of: %s", phrase));
		
		List<Entity<ActivityDefinition>> queried = activityLogic.search("just climbed a mountain with @saiedm at 4").ranked();
		for (Entity<ActivityDefinition> q: queried) {
			this.logger.info(String.format("%s: %s", q.get(ActivityDefinition.name), q.get(ActivityDefinition.concept).get(Concept.path)));
		}
		
		if (activity == null) {
			this.logger.info(String.format("2a) doing concept query for: %s", intention));
			
			List<Entity<Concept>> concepts = activityLogic.findConcepts(intention);
			Entity<Concept> top = concepts.get(0);
			
			List<Entity<ActivityDefinition>> existingTop = activityLogic.search(top.get(Concept.name)).ranked();
			if (!existingTop.isEmpty()) {
				activity = existingTop.get(0);
				this.logger.info(String.format("2b) found activity under top ranked match: %s: %s", activity.get(ActivityDefinition.name), activity.get(ActivityDefinition.concept).get(Concept.path)));
			} else {
				this.logger.info(String.format("2b) creating new activity under top ranked match: %s", top.get(Concept.path)));
				activity = activityLogic.create(top.get(Concept.name), top);
			}
			
			this.logger.info(String.format("3) creating ping for: %s: %s", activity.get(ActivityDefinition.name), activity.get(ActivityDefinition.concept).get(Concept.path)));
			
			Entity<Ping> ping = pingLogic.create(user, activity, phrase, 100.0D, 100.0D, null, new MultiMap<String, String>() {{
				add("with", "@saeidm");
				add("what", "mountain");
			}});
			
			this.logger.info(String.format("4) created ping: %s", ping.getKey()));
		}
	}
	
}
