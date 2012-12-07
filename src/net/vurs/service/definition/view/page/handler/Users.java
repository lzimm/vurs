package net.vurs.service.definition.view.page.handler;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.logic.controller.UserLogic;
import net.vurs.service.definition.view.page.PageHandler;
import net.vurs.service.definition.view.page.PageRequest;

public class Users extends PageHandler {
	
	private UserLogic userLogic = null;

	@Override
	public void setup(LogicService logicService) {
		super.setup(logicService);
		this.userLogic = this.logicService.get(UserLogic.class);
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.USER)
	@Routing(patterns={"/home"})
	public void home(PageRequest request) {
		Entity<User> root = userLogic.get("");
		request.addRenderVar("root", root);
		
		request.renderTemplate("users/home.html");
	}
	
	@AuthenticationPolicy(level=AuthenticationLevel.USER)
	@Routing(patterns={"/user/checkin"})
	public void checkin(PageRequest request) {
		Entity<User> root = userLogic.get("");
		request.addRenderVar("root", root);
		
		request.renderTemplate("users/listing.html");
	}
	
}
