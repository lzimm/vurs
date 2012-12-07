package net.vurs.service.definition.view.page.handler;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.service.definition.view.page.PageHandler;
import net.vurs.service.definition.view.page.PageRequest;

public class Auth extends PageHandler {

	private AuthenticationLogic authLogic = null;
	
	@Override
	public void init() {
		this.authLogic = this.logicService.get(AuthenticationLogic.class);
	}

	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/auth/login"})
	public void login(PageRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Entity<User> user = this.authLogic.login(username, password);
		
		if (user != null) {
			String redirectPath = (String) request.getSessionVariable(PageRequest.REDIRECT_PATH, "/");
			request.setUser(user);
			request.redirectTo(redirectPath);
			return;
		}
		
		request.renderTemplate("auth/login.html");
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/auth/register"})
	public void register(PageRequest request) {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Entity<User> user = this.authLogic.register(username, email, password);
		
		if (user != null) {
			request.setUser(user);
			
			String redirectPath = (String) request.getSessionVariable(PageRequest.REDIRECT_PATH, "/");
			
			if (redirectPath == null) {
				redirectPath = "/home";
			}
			
			request.redirectTo(redirectPath);
			return;
		}	
		
		request.renderTemplate("auth/register.html");
	}
	
}
