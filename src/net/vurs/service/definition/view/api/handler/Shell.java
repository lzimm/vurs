package net.vurs.service.definition.view.api.handler;

import javax.servlet.http.HttpServletResponse;

import bsh.Interpreter;
import net.sf.json.JSONSerializer;
import net.vurs.annotation.AuthenticationPolicy;
import net.vurs.annotation.Routing;
import net.vurs.annotation.AuthenticationPolicy.AuthenticationLevel;
import net.vurs.service.definition.view.api.APIHandler;
import net.vurs.service.definition.view.api.APIRequest;
import net.vurs.util.ErrorControl;

public class Shell extends APIHandler {

	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/shell"})
	public void exec(APIRequest request) {
		String script = request.getParameter("script");
		
		try {
			Interpreter i = new Interpreter();
			i.set("logicService", this.logicService);
			Object res = i.eval(script);
			
			this.logger.info(String.format("\nExecuted script: %s\nResponse: %s", 
											script, 
											JSONSerializer.toJSON(res)));
			
			request.sendResponse(res);
		} catch (Exception e) {
			ErrorControl.logException(e);
			request.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	
}
