package net.vurs.tests;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.LogicService;
import net.vurs.transaction.Transaction;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

public class TestService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private EntityService entityService = null;
	private LogicService logicService = null;
	
	public TestService(EntityService entityService, LogicService logicService) {
		this.logger.info("Loading test service");
		this.entityService = entityService;
		this.logicService = logicService;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(TestRunner.class));

		while (itr.hasNext()) {
			try {
				Class<? extends TestRunner> cls = (Class<? extends TestRunner>) itr.next();
				if (cls.equals(TestRunner.class)) continue;
				
				TestRunner runner = cls.newInstance();
				runner.setup(this.entityService, this.logicService);
				
				for (Method m: cls.getDeclaredMethods()) {
					if (m.getParameterTypes().length > 0) continue;
					
					Transaction transaction = this.entityService.startGlobalTransaction();
					m.invoke(runner);
					transaction.finish();
				}
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}

}
