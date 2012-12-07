package net.vurs.service.definition.neighbourhood;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.MultiMap;
import net.vurs.entity.Entity;
import net.vurs.entity.ProxyEntity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.UserData;
import net.vurs.entity.definition.activity.ActivityPinged;
import net.vurs.entity.definition.badge.ActivityBadgeCriteria;
import net.vurs.entity.definition.badge.ActivityBadgeIndex;
import net.vurs.entity.definition.badge.BadgeDefinition;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.entity.manager.UserDataManager;
import net.vurs.service.definition.entity.manager.badge.ActivityBadgeCriteriaManager;
import net.vurs.service.definition.entity.manager.badge.ActivityBadgeIndexManager;
import net.vurs.service.definition.entity.manager.badge.BadgeDefinitionManager;
import net.vurs.service.definition.entity.manager.badge.BadgeEarnedManager;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

public class BadgeController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;

	private ActivityBadgeIndexManager activityBadgeIndexManager = null;
	private BadgeDefinitionManager badgeDefinitionManager = null;
	private UserDataManager userDataManager = null;
	private BadgeEarnedManager badgeEarnedManager = null;
	private ActivityBadgeCriteriaManager activityBadgeCriteriaManager = null;
	
	private List<LogicalBadge> logicalBadges = null;
	
	public BadgeController(EntityService entityService) {
		this.entityService  = entityService;
		
		this.activityBadgeIndexManager = this.entityService.getManager(ActivityBadgeIndex.class, ActivityBadgeIndexManager.class);
		this.badgeDefinitionManager = this.entityService.getManager(BadgeDefinition.class, BadgeDefinitionManager.class);
		this.userDataManager = this.entityService.getManager(UserData.class, UserDataManager.class);
		this.badgeEarnedManager = this.entityService.getManager(BadgeEarned.class, BadgeEarnedManager.class);
		this.activityBadgeCriteriaManager = this.entityService.getManager(ActivityBadgeCriteria.class, ActivityBadgeCriteriaManager.class);
		
		this.initLogicalBadges();
	}

	@SuppressWarnings("unchecked")
	private void initLogicalBadges() {
		this.logger.info("Setting up logical badges");
		
		this.logicalBadges = new ArrayList<LogicalBadge>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(LogicalBadge.class));

		while (itr.hasNext()) {
			Class<? extends LogicalBadge> cls = (Class<? extends LogicalBadge>) itr.next();
			
			try {
				this.logger.info(String.format("Found logical badge class %s", cls.getName()));
				
				LogicalBadge badge = cls.newInstance();
				badge.init(this.entityService);
				
				this.logicalBadges.add(badge);
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}

	public List<Entity<BadgeEarned>> earnBadges(Entity<Ping> ping) {
		List<Entity<BadgeEarned>> earnedList = new ArrayList<Entity<BadgeEarned>>();
		
		List<Entity<BadgeDefinition>> definitionList = new ArrayList<Entity<BadgeDefinition>>();
		
		List<Entity<ActivityBadgeIndex>> activityIndexList = this.activityBadgeIndexManager.query(ActivityBadgeIndex.activity, ping.get(Ping.activity));
		for (Entity<ActivityBadgeIndex> index: activityIndexList) {
			definitionList.add(new ProxyEntity<BadgeDefinition>(this.entityService, BadgeDefinition.class, index.get(ActivityBadgeIndex.activity).getKey()));
		}
		
		this.badgeDefinitionManager.materialize(definitionList);
		
		for (Entity<BadgeDefinition> badgeDefinition: definitionList) {
			Entity<BadgeEarned> earned = this.earnBadge(badgeDefinition, ping);
			if (earned != null) {
				earnedList.add(earned);
			}
		}
		
		
		for (LogicalBadge logicalBadge: this.logicalBadges) {
			Entity<BadgeEarned> earned = logicalBadge.earn(ping);
			if (earned != null) {
				earnedList.add(earned);
			}
		}
		
		return earnedList;
	}
	
	public Entity<BadgeEarned> earnBadge(Entity<BadgeDefinition> badgeDefinition, Entity<Ping> ping) {
		Entity<UserData> userData = this.userDataManager.get(ping.get(Ping.user).getKey());
		List<Entity<BadgeEarned>> badgeHistory = userData.get(UserData.badges);
		List<Entity<ActivityPinged>> activityHistory = userData.get(UserData.activities);
		
		if (!checkBadgePrerequisits(badgeDefinition, badgeHistory)) return null;
		if (!checkActivityPrerequisits(badgeDefinition, activityHistory)) return null;
		
		Entity<BadgeEarned> badge = this.badgeEarnedManager.create();
		badge.set(BadgeEarned.definition, badgeDefinition);
		badge.set(BadgeEarned.ping, ping);
		badge.set(BadgeEarned.user, ping.get(Ping.user));
		this.badgeEarnedManager.save(badge);
		this.userDataManager.insert(ping.get(Ping.user).getKey(), UserData.badges, badge);
		
		return badge;
	}

	private boolean checkBadgePrerequisits(Entity<BadgeDefinition> badgeDefinition, List<Entity<BadgeEarned>> badgeHistory) {
		Entity<BadgeDefinition> parent = badgeDefinition.get(BadgeDefinition.parent);
		
		if (parent != null) {
			boolean foundParent = false;
			for (Entity<BadgeEarned> b: badgeHistory) {
				if (b.get(BadgeEarned.definition).getKey().equals(parent.getKey())) {
					foundParent = true;
				} else if (b.get(BadgeEarned.definition).getKey().equals(badgeDefinition.getKey())) {
					return false;
				}
			}
			
			if (!foundParent) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkActivityPrerequisits(Entity<BadgeDefinition> badgeDefinition, List<Entity<ActivityPinged>> activityHistory) {
		List<Entity<ActivityBadgeCriteria>> rawCriterion = this.activityBadgeCriteriaManager.query(ActivityBadgeCriteria.badge, badgeDefinition);
		MultiMap<Integer, Entity<ActivityBadgeCriteria>> equivalentCriterion = new MultiMap<Integer, Entity<ActivityBadgeCriteria>>();
		for (Entity<ActivityBadgeCriteria> criteria: rawCriterion) {
			equivalentCriterion.add(criteria.get(ActivityBadgeCriteria.equivalenceOrder), criteria);
		}
		
		for (List<Entity<ActivityBadgeCriteria>> criterion: equivalentCriterion.values()) {
			boolean metCriteria = false;
			
			for (Entity<ActivityBadgeCriteria> criteria: criterion) {
				int pingCount = criteria.get(ActivityBadgeCriteria.pingCount);
				for (Entity<ActivityPinged> pinged: activityHistory) {
					if (pinged.get(ActivityPinged.definition).getKey().equals(criteria.get(ActivityBadgeCriteria.activity).getKey())) {
						--pingCount;
					}
				}
				
				if (pingCount <= 0) {
					metCriteria = true;
					break;
				}
			}
			
			if (!metCriteria) {
				return false;
			}
		}
		
		return true;
	}
	
}
