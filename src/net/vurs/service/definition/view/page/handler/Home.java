package net.vurs.service.definition.view.page.handler;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.service.definition.view.page.PageHandler;
import net.vurs.service.definition.view.page.PageRequest;

public class Home extends PageHandler {
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/"})
	public void index(PageRequest request) {		
		request.renderTemplate("home/index.html");
	}
	
}
