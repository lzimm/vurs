package net.vurs.service.definition.view.api;

import java.lang.reflect.Method;
import java.util.List;

import com.google.code.regex.NamedPattern;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.service.definition.view.ViewHandler;
import net.vurs.service.definition.view.ViewMethod;
import net.vurs.service.definition.view.ViewRequest;

public class APIMethod extends ViewMethod<APIView> {

	public APIMethod(Method method, ViewHandler<APIView> handler,
			List<NamedPattern> patterns, AuthenticationLogic authLogic) {
		super(method, handler, patterns, authLogic);
	}
	
	public boolean authenticate(ViewRequest<APIView> req) {
		Entity<User> user = req.getUser();
		
		if (user == null) {
			user = authLogic.fromToken(req.getParameter("key", null), req.getParameter("token", null));
			req.setUser(user);
		}
		
		return authLogic.authenticate(method.getAnnotation(AuthenticationPolicy.class).level(), user);
	}

}
