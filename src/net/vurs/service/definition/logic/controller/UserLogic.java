package net.vurs.service.definition.logic.controller;

import java.util.List;

import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.entity.manager.UserManager;
import net.vurs.service.definition.logic.LogicController;

public class UserLogic extends LogicController {

	private UserManager userManager = null;

	@Override
	public void init() {
		this.userManager = this.entityService.getManager(User.class, UserManager.class);
	}
	
	public Entity<User> get(String key) {
		List<Entity<User>> found = this.userManager.query(User.name, key);
		if (found.size() > 0) {
			return found.get(0);
		} else {
			return null;
		}
	}
	
	public Entity<User> create(String key, String email) {
		Entity<User> user = this.userManager.create();
		
		user.set(User.email, email);
		user.set(User.name, key);
		
		if (this.userManager.save(user)) {
			return user;
		} else {
			return null;
		}
	}
	
	public void delete(String key) {
		this.userManager.delete(key);
	}
	
	public void save(Entity<User> user) {
		this.userManager.save(user);
	}
	
}
