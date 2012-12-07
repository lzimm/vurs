package net.vurs.service.definition.neighbourhood;

import net.vurs.entity.Entity;
import net.vurs.entity.definition.badge.BadgeDefinition;

public class LogicalBadgeDefinition {

	private Entity<BadgeDefinition> parent;
	private String name;
	private String description;
	private String asset;
	
	public LogicalBadgeDefinition(Entity<BadgeDefinition> parent, String name, String description, String asset) {
		this.parent = parent;
		this.name = name;
		this.description = description;
		this.asset = asset;
	}
	
	public Entity<BadgeDefinition> getParent() { return this.parent; }
	public String getName() { return this.name; }
	public String getDescription() { return this.description; }
	public String getAsset() { return this.asset; }
	
}
