package net.vurs.service.definition.view.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.view.ViewManager;
import net.vurs.transaction.Transaction;

public class APIManager extends ViewManager<APIView> {

	public APIManager(EntityService entityService, LogicService logicService) {
		super(entityService, logicService);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Transaction transaction = this.entityService.startGlobalTransaction();
		
		APIRequest apiRequest = new APIRequest(request, response);
		handle(apiRequest);
		
		transaction.finish();
	}
	
}
