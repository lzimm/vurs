package net.vurs.service.definition.neighbourhood;

import java.util.List;

import net.vurs.entity.Entity;
import net.vurs.entity.definition.badge.BadgeEarned;

public class PingResult {

	private List<Entity<BadgeEarned>> badges = null;
	
	public PingResult(List<Entity<BadgeEarned>> badges) {
		this.badges = badges;
	}
	
	public List<Entity<BadgeEarned>> getBadges() { return this.badges; }

}