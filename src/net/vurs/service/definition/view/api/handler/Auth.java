package net.vurs.service.definition.view.api.handler;

import java.util.HashMap;
import java.util.Map;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.service.definition.view.api.APIHandler;
import net.vurs.service.definition.view.api.APIRequest;

public class Auth extends APIHandler {

	private AuthenticationLogic authLogic = null;

	@Override
	public void setup(LogicService logicService) {
		super.setup(logicService);
		this.authLogic = this.logicService.get(AuthenticationLogic.class);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/auth/login"})
	public void login(APIRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("result", false);
		
		Entity<User> user = this.authLogic.login(username, password);
		
		if (user != null) {
			request.setUser(user);
			res.put("result", true);
		}	
		
		request.sendResponse(res);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/auth/register"})
	public void register(APIRequest request) {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("result", false);
		
		Entity<User> user = this.authLogic.register(username, email, password);
		
		if (user != null) {
			request.setUser(user);
			res.put("result", true);
		}	
		
		request.sendResponse(res);
	}
	
}
