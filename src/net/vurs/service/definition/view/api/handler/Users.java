package net.vurs.service.definition.view.api.handler;

import java.util.HashMap;
import java.util.Map;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.logic.controller.UserLogic;
import net.vurs.service.definition.view.api.APIHandler;
import net.vurs.service.definition.view.api.APIRequest;

public class Users extends APIHandler {

	private UserLogic userLogic = null;

	@Override
	public void init() {
		this.userLogic  = this.logicService.get(UserLogic.class);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/user/checkin"})
	public void checkin(APIRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("result", false);
		
		Entity<User> user = this.userLogic.get(username);
		
		if (user != null) {
			String userPassword = user.get(User.password);
			if (userPassword != null && password.equals(userPassword)) {
				request.setUser(user);
				res.put("result", true);
			}
		}	
		
		request.sendResponse(res);
	}
	
}
