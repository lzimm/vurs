package net.vurs.service.definition.neighbourhood;

import java.util.List;

import net.vurs.conn.sql.operation.predicate.SQLFieldPredicate;
import net.vurs.conn.sql.operation.predicate.SQLPredicate;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.entity.definition.UserData;
import net.vurs.entity.definition.badge.BadgeDefinition;
import net.vurs.entity.definition.badge.BadgeEarned;
import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.entity.manager.UserDataManager;
import net.vurs.service.definition.entity.manager.badge.BadgeDefinitionManager;
import net.vurs.service.definition.entity.manager.badge.BadgeEarnedManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LogicalBadge {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	protected UserDataManager userDataManager = null;
	protected BadgeEarnedManager badgeEarnedManager = null;
	
	protected Entity<BadgeDefinition> definition = null;
	
	public void init(EntityService entityService) {
		this.entityService = entityService;
		
		this.userDataManager = this.entityService.getManager(UserData.class, UserDataManager.class);
		this.badgeEarnedManager = this.entityService.getManager(BadgeEarned.class, BadgeEarnedManager.class);
		
		this.initDefinition();
	}
	
	private void initDefinition() {
		this.entityService.startGlobalTransaction();
		
		LogicalBadgeDefinition def = this.define();
		
		SQLPredicate<BadgeDefinition> predicate = new SQLFieldPredicate<BadgeDefinition, Integer>(BadgeDefinition.type, BadgeDefinition.Type.LOGICAL.getIndex())
																.and(new SQLFieldPredicate<BadgeDefinition, String>(BadgeDefinition.name, def.getName()))
																.and(new SQLFieldPredicate<BadgeDefinition, String>(BadgeDefinition.description, def.getDescription()))
																.and(new SQLFieldPredicate<BadgeDefinition, String>(BadgeDefinition.asset, def.getAsset()));
		
		if (def.getParent() != null) {
			predicate = predicate.and(new SQLFieldPredicate<BadgeDefinition, Entity<BadgeDefinition>>(BadgeDefinition.parent, def.getParent()));
		}
		
		List<Entity<BadgeDefinition>> definitions = this.entityService.getManager(BadgeDefinition.class, BadgeDefinitionManager.class).query(predicate);
		
		if (definitions.size() > 1) {
			this.logger.warn(String.format("Found multiple definitions for logical badge %s, using %s", this.getClass().getName(), definitions.get(0).getKey()));
			this.definition = definitions.get(0);
		} else if (definitions.size() == 1) {
			this.logger.info(String.format("Found definition %s for logical badge %s", definitions.get(0).getKey(), this.getClass().getName()));
			this.definition = definitions.get(0);
		} else {
			this.logger.info(String.format("Couldn't find existing definition for logical badge %s, creating new one", this.getClass().getName()));
			
			definition = this.entityService.getManager(BadgeDefinition.class, BadgeDefinitionManager.class).create();
			definition.set(BadgeDefinition.name, def.getName());
			definition.set(BadgeDefinition.description, def.getDescription());
			definition.set(BadgeDefinition.asset, def.getAsset());
			definition.set(BadgeDefinition.type, BadgeDefinition.Type.LOGICAL.getIndex());
			
			if (def.getParent() != null) {
				definition.set(BadgeDefinition.parent, def.getParent());
			}
			
			this.entityService.getManager(BadgeDefinition.class, BadgeDefinitionManager.class).save(definition);
		}
		
		this.entityService.getGlobalTransaction().finish();
	}

	public Entity<BadgeEarned> earn(Entity<Ping> ping) {
		if (this.check(ping)) {
			Entity<BadgeEarned> badge = this.badgeEarnedManager.create();
			badge.set(BadgeEarned.definition, this.definition);
			badge.set(BadgeEarned.ping, ping);
			badge.set(BadgeEarned.user, ping.get(Ping.user));
			this.badgeEarnedManager.save(badge);
			this.userDataManager.insert(ping.get(Ping.user).getKey(), UserData.badges, badge);
			
			return badge;
		} else {
			return null;
		}
	}
	
	protected abstract LogicalBadgeDefinition define();
	protected abstract boolean check(Entity<Ping> ping);
	
}
