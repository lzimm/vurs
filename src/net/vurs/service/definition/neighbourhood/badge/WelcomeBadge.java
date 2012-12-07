package net.vurs.service.definition.neighbourhood.badge;

import net.vurs.entity.Entity;
import net.vurs.entity.definition.Ping;
import net.vurs.service.definition.neighbourhood.LogicalBadge;
import net.vurs.service.definition.neighbourhood.LogicalBadgeDefinition;

public class WelcomeBadge extends LogicalBadge {

	@Override
	public LogicalBadgeDefinition define() {
		return new LogicalBadgeDefinition(null, "Welcome", "Your very first badge!", "welcome.png");
	}
	
	@Override
	protected boolean check(Entity<Ping> ping) {
		return false;
	}

}
