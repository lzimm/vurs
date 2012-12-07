package net.vurs.service.definition.view;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.util.ErrorControl;

import com.google.code.regex.NamedMatcher;
import com.google.code.regex.NamedPattern;

public class ViewMethod<T extends ViewType> {

	protected Method method = null;
	protected ViewHandler<T> handler = null;
	protected List<NamedPattern> patterns = null;
	protected AuthenticationLogic authLogic = null;
	
	public ViewMethod(Method method, ViewHandler<T> handler, List<NamedPattern> patterns, AuthenticationLogic authLogic) {
		this.method = method;
		this.handler = handler;
		this.patterns = patterns;
		this.authLogic = authLogic;
	}
	
	public boolean match(ViewRequest<T> req) {
		for (NamedPattern pattern: patterns) {
			NamedMatcher m = pattern.matcher(req.getPath());
			if (m.matches()) {
				for (String group: pattern.groupNames()) {
					req.setParameter(group, m.group(group));
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public void run(ViewRequest<T> req) {
		try {
			boolean authenticated = true;
			
			if (method.isAnnotationPresent(AuthenticationPolicy.class)) {
				authenticated = authenticate(req);
			}
			
			if (authenticated) {
				method.invoke(handler, req);
			} else {
				req.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public boolean authenticate(ViewRequest<T> req) {
		Entity<User> user = req.getUser();
		return authLogic.authenticate(method.getAnnotation(AuthenticationPolicy.class).level(), user);
	}
	
}
