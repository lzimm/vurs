package net.vurs.service.definition.logic.controller;

import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.entity.definition.authentication.UserToken;
import net.vurs.service.definition.entity.manager.authentication.UserTokenManager;
import net.vurs.service.definition.logic.LogicController;

public class AuthenticationLogic extends LogicController {

	private UserTokenManager tokenManager = null;
	private UserLogic userLogic = null;
	
	@Override
	public void init() {
		this.tokenManager = this.entityService.getManager(UserToken.class, UserTokenManager.class);
		this.userLogic  = this.logicService.get(UserLogic.class);
	}
	
	public boolean authenticate(AuthenticationLevel level, Entity<User> user) {
		if (level == AuthenticationLevel.PUBLIC) {
			return true;
		}
		
		if (level == AuthenticationLevel.USER && user != null) {
			return true;
		}
		
		if (level == AuthenticationLevel.ADMIN && user != null) {
			Boolean isAdmin = user.get(User.isAdmin);
			if (isAdmin != null && isAdmin.booleanValue()) {
				return true;
			}
		}
		
		return false;
	}

	public Entity<User> login(String username, String password) {
		Entity<User> user = this.userLogic.get(username);
		
		if (user != null) {
			String userPassword = user.get(User.password);
			if (userPassword != null && password == null || !password.equals(userPassword)) {
				user = null;
			}
		}
		
		return user;
	}
	
	public Entity<User> register(String username, String email, String password) {
		Entity<User> user = this.userLogic.get(username);
		
		if (user == null) {
			user = this.userLogic.create(username, email);
			user.set(User.password, password);
			this.userLogic.save(user);
		}
		
		return user;
	}
	
	public Entity<User> fromToken(String key, String token) {
		if (key == null || token == null) return null;
		
		Entity<User> user = null;
		Entity<UserToken> userToken = this.tokenManager.get(token);
		
		if (userToken != null && userToken.get(UserToken.key).equals(key)) {
			user = userToken.get(UserToken.user);
		}
		
		return user;
	}
	
}
