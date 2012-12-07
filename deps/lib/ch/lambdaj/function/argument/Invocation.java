// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.argument;

import java.lang.ref.*;
import java.lang.reflect.*;
import java.util.*;

import ch.lambdaj.util.*;

/**
 * Registers a method invocation
 * @author Mario Fusco
 */
class Invocation {

	private final Class<?> invokedClass;
	private final Method invokedMethod;
	private String invokedPropertyName;
	private List<WeakReference<Object>> weakArgs;

    Invocation(Class<?> invokedClass, Method invokedMethod, Object[] args) {
		this.invokedClass = invokedClass;
		this.invokedMethod = invokedMethod;
		if (args != null && args.length > 0) {
			weakArgs = new ArrayList<WeakReference<Object>>();
			for (Object arg : args) weakArgs.add(new WeakReference<Object>(arg));
		}
	}
	
	private Object[] getConcreteArgs() {
		if (weakArgs == null) return null;
		Object[] args = new Object[weakArgs.size()];
		int i = 0;
		for (WeakReference<Object> weakArg : weakArgs) args[i++] = weakArg.get();
		return args;
	}

	Class<?> getInvokedClass() {
		return invokedClass;
	}

	Method getInvokedMethod() {
		return invokedMethod;
	}
	
	Class<?> getReturnType() {
		return invokedMethod.getReturnType();
	}
	
	String getInvokedPropertyName() {
		if (invokedPropertyName == null) invokedPropertyName = IntrospectionUtil.getPropertyName(invokedMethod);
		return invokedPropertyName;
	}

	Object invokeOn(Object object) throws InvocationException {
		try {
			return object == null ? null : invokedMethod.invoke(object, getConcreteArgs());
		} catch (Exception e) {
			throw new InvocationException(e, invokedMethod, object);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(invokedMethod.toString());
		Object[] args = getConcreteArgs();
		if (args != null) {
			sb.append(" with args ");
			for (int i = 0; i < args.length; i++) sb.append(i == 0 ? "" : ", ").append(args[i]);
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		int hashCode = 13 * invokedClass.hashCode();
		if (invokedMethod != null) hashCode += 17 * invokedMethod.hashCode();
		if (weakArgs != null) hashCode += 19 + weakArgs.size();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Invocation)) return false;
		Invocation otherInvocation = (Invocation)object;
        return areNullSafeEquals(invokedClass, otherInvocation.getInvokedClass()) &&
               areNullSafeEquals(invokedMethod, otherInvocation.getInvokedMethod()) &&
               Arrays.equals(getConcreteArgs(), otherInvocation.getConcreteArgs());
	}
	
	private boolean areNullSafeEquals(Object first, Object second) {
		return (first == null && second == null) || (first != null && second != null && first.equals(second));
	}
}
