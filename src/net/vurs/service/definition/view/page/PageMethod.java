package net.vurs.service.definition.view.page;

import java.lang.reflect.Method;
import java.util.List;

import com.google.code.regex.NamedPattern;

import net.vurs.service.definition.logic.controller.AuthenticationLogic;
import net.vurs.service.definition.view.ViewHandler;
import net.vurs.service.definition.view.ViewMethod;

public class PageMethod extends ViewMethod<PageView> {

	public PageMethod(Method method, ViewHandler<PageView> handler,
			List<NamedPattern> patterns, AuthenticationLogic authLogic) {
		super(method, handler, patterns, authLogic);
	}

}
