package net.vurs.service.definition.view;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vurs.annotation.Routing;
import net.vurs.annotation.ViewMapping;
import net.vurs.service.definition.EntityService;
import net.vurs.service.definition.LogicService;
import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.transaction.Transaction;
import net.vurs.util.ClassWalker;
import net.vurs.util.ClassWalkerFilter;
import net.vurs.util.ErrorControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.regex.NamedPattern;

public class ViewManager<T extends ViewType> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Class<T> type;
	protected EntityService entityService;
	protected LogicService logicService;
	protected AuthenticationLogic authLogic;
	
	protected Class<? extends ViewHandler<T>> handlerType;
	protected Class<? extends ViewMethod<T>> methodType;
	protected Class<? extends ViewRequest<T>> requestType;
	
	protected ViewMapping viewMapping;

	protected Map<String, ViewMethod<T>> methodMap;
	protected List<ViewMethod<T>> methods;
	
	public ViewManager(EntityService entityService, LogicService logicService) {
		this.logger.info(String.format("Loading %s manager", getClass().getSimpleName()));

		this.entityService = entityService;
		this.logicService = logicService;
		this.authLogic = this.logicService.get(AuthenticationLogic.class);
	}
	
	@SuppressWarnings("unchecked")
	public void setup(Class<? extends ViewType> type, Class<? extends ViewHandler<?>> handlerType, Class<? extends ViewMethod<?>> methodType, Class<? extends ViewRequest<?>> requestType) {
		this.type = (Class<T>) type;
		this.handlerType = (Class<? extends ViewHandler<T>>) handlerType;
		this.methodType = (Class<? extends ViewMethod<T>>) methodType;
		this.requestType = (Class<? extends ViewRequest<T>>) requestType;
		this.viewMapping = this.type.getAnnotation(ViewMapping.class);
		
		setupHandlers();
	}
	
	@SuppressWarnings("unchecked")
	private void setupHandlers() {
		this.logger.info("Setting up handlers");
		
		this.methodMap = new HashMap<String, ViewMethod<T>>();
		this.methods = new ArrayList<ViewMethod<T>>();
		
		ClassWalkerFilter filter = ClassWalkerFilter.extendingWithParam(this.handlerType, this.type);
		if (this.handlerType.getGenericSuperclass() instanceof ParameterizedType) {
			filter = ClassWalkerFilter.extending(this.handlerType);
		}
				
		Iterator<Class<?>> itr = new ClassWalker(filter);
		
		String handlerPackage = new StringBuilder(getClass().getPackage().getName()).append(".handler").toString();
		while (itr.hasNext()) {
			Class<?> cls = itr.next();
			String path = new StringBuilder(this.viewMapping.prefixPath()).append(
											cls.getName().substring(handlerPackage.length())
											.replace('.', '/')
											.toLowerCase()).toString();
			
			try {
				ViewHandler<T> handler = (ViewHandler<T>) cls.newInstance();
				handler.setup(this.logicService);
				
				for (Method method: cls.getMethods()) {
					Type[] params = method.getGenericParameterTypes();
					
					if (params.length != 1) continue;
					
					Class<?> paramType = null;
					if (params[0] instanceof Class) {
						paramType = (Class<?>) params[0];
					} else if (params[0] instanceof ParameterizedType) {
						paramType = (Class<?>) ((ParameterizedType) params[0]).getRawType(); 
					} else {
						continue;
					}
					
					if (!paramType.equals(this.requestType)) continue;
					if (!Modifier.isPublic(method.getModifiers())) continue;
					
					method.setAccessible(true);
					
					String fullPath = new StringBuilder(path)
												.append('/')
												.append(method.getName().toLowerCase())
												.toString();
					
					this.logger.info(String.format("Found view method for: %s", fullPath));

					final List<NamedPattern> patterns = new ArrayList<NamedPattern>();
					if (method.isAnnotationPresent(Routing.class)) {
						String[] patternStrings = method.getAnnotation(Routing.class).patterns();
						for (String patternString: patternStrings) {
							patternString = String.format("%s%s", this.viewMapping.prefixPath(), patternString);
							this.logger.info(String.format("Found view method for: %s", patternString));
							patterns.add(NamedPattern.compile(patternString));
						}
					}					
					
					Constructor<? extends ViewMethod<T>> ctor = this.methodType.getConstructor(Method.class, ViewHandler.class, List.class, AuthenticationLogic.class);
					ViewMethod<T> viewMethod = ctor.newInstance(method, handler, patterns, authLogic);
					
					this.methodMap.put(fullPath, viewMethod);
					this.methods.add(viewMethod);
				}
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		}
	}
	
	public EntityService getEntityService() { return this.entityService; }
	public LogicService getLogicService() { return this.logicService; }

	public void handle(ViewRequest<T> request) {
		ViewMethod<T> method = this.methodMap.get(request.getPath());
		if (method != null) {
			method.run(request);
		} else {
			Iterator<ViewMethod<T>> itr = this.methods.iterator();
			while (itr.hasNext()) {
				method = itr.next();
				if (method.match(request)) {
					method.run(request);
					return;
				}
			}
			
			request.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Transaction transaction = this.entityService.startGlobalTransaction();
		
		ViewRequest<T> viewRequest = new ViewRequest<T>(request, response);
		handle(viewRequest);
		
		transaction.finish();
	}
	
}
