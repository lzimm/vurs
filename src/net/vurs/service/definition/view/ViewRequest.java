package net.vurs.service.definition.view;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vurs.common.MultiMap;
import net.vurs.entity.Entity;
import net.vurs.entity.definition.User;
import net.vurs.util.ErrorControl;

public class ViewRequest<T extends ViewType> {
	private static final String USER = "USER";
	
	public HttpServletRequest request = null;
	public HttpServletResponse response = null;
	public Map<String, String> routeParams = null;
	public MultiMap<String, String> parameterMap = null;
	
	public ViewRequest(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.routeParams = new HashMap<String, String>();
		this.parameterMap = this.buildParameterMap();
	}
	
	public String getPath() {
		return this.request.getRequestURI().toLowerCase();
	}
	
	public String getIP() {
		return this.request.getRemoteAddr();
	}

	public void sendError(int code) {
		try {
			this.response.sendError(code);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public String getParameter(String key) { return this.getParameter(key, ""); }
	public String getParameter(String key, String defaultValue) {
		if (this.routeParams.containsKey(key)) {
			return this.routeParams.get(key);
		}

		return this.parameterMap.first(key, defaultValue);
	}
	
	public String removeParameter(String key) { return this.removeParameter(key, ""); }
	public String removeParameter(String key, String defaultValue) {
		return this.parameterMap.getFirstAndRemove(key, defaultValue);
	}
	
	public MultiMap<String, String> getParameterMap() {
		return this.parameterMap;
	}
	
	public MultiMap<String, String> buildParameterMap() {
		MultiMap<String, String> ret = new MultiMap<String, String>();
		
		Enumeration<?> keys = this.request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			for (String val: this.request.getParameterValues(key)) {
				ret.add(key, val);
			}
		}
		
		return ret;
	}

	public void setParameter(String key, String value) {
		this.routeParams.put(key, value);
	}

	public void setSessionVariable(String key, Object object) {
		this.request.getSession(true).setAttribute(key, object);
	}
	
	public Object getSessionVariable(String key) { return getSessionVariable(key, null); }
	public Object getSessionVariable(String key, Object def) {
		Object ret = this.request.getSession(true).getAttribute(key);
		if (ret == null) ret = def;
		return ret;
	}
	
	public void setUser(Entity<User> user) {
		this.setSessionVariable(USER, user);
	}
	
	@SuppressWarnings("unchecked")
	public Entity<User> getUser() {
		return (Entity<User>) this.getSessionVariable(USER);
	}
	
}
