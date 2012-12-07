package net.vurs.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.service.ServiceManager;
import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.ViewService;
import net.vurs.tests.TestService;

public class Setup extends HttpServlet {
	private static final long serialVersionUID = -89077422071834551L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setupServices(config);
	}
	
	private void setupServices(ServletConfig config) {
		this.logger.info("Loading services");
		
		ServiceManager serviceManager = new ServiceManager();
		LogicService logicService = serviceManager.getService(LogicService.class);
		ViewService viewService = serviceManager.getService(ViewService.class);
		
		config.getServletContext().setAttribute("logic", logicService);
		config.getServletContext().setAttribute("views", viewService);
		
		EntityService entityService = serviceManager.getService(EntityService.class);
		TestService testService = new TestService(entityService, logicService);
		testService.run();
	}

}