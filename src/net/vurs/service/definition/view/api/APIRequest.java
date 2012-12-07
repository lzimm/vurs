package net.vurs.service.definition.view.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.vurs.service.definition.view.ViewRequest;
import net.vurs.util.ErrorControl;

public class APIRequest extends ViewRequest<APIView> {

	public APIRequest(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}
	
	public void sendResponse(Object res) {
		try{
			JSON json = null;
			
			if (res instanceof JSON) {
				json = (JSON) res;
			} else {
				json = JSONSerializer.toJSON(res);
			}
			
			this.response.getWriter().write(json.toString());
			this.response.getWriter().flush();
			this.response.getWriter().close();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

}
