// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.util;

import java.lang.reflect.*;

/**
 * This class consists exclusively of static methods that offer some introspection facilities.
 * @author Mario Fusco
 */
public final class IntrospectionUtil {

	private IntrospectionUtil() {}

	public static String getPropertyName(Method invokedMethod) {
		String methodName = invokedMethod.getName();
		if (methodName.startsWith("get") || methodName.startsWith("set")) methodName = methodName.substring(3);
		else if (methodName.startsWith("is")) methodName = methodName.substring(2);
		return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
	}
	
	public static Object getPropertyValue(Object bean, String propertyName) {
		if (bean == null) return null;
		int dotPos = propertyName.indexOf('.');
		if (dotPos > 0) return getPropertyValue(getPropertyValue(bean, propertyName.substring(0, dotPos)), propertyName.substring(dotPos + 1));

		String accessorName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		try {
			return bean.getClass().getMethod("get" + accessorName).invoke(bean, (Object[]) null);
		} catch (Exception e) {
			return getBooleanPropertyValue(bean, propertyName, accessorName);
		}
	}
	
	private static Object getBooleanPropertyValue(Object bean, String propertyName, String accessorName) {
		try {
			return bean.getClass().getMethod("is" + accessorName).invoke(bean, (Object[]) null);
		} catch (Exception e) {
			return getPlainPropertyValue(bean, propertyName);
		}
	}
	
	private static Object getPlainPropertyValue(Object bean, String propertyName) {
		try {
			return bean.getClass().getMethod(propertyName).invoke(bean, (Object[]) null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public static <T> Constructor<T> findConstructor(Class<T> clazz, Object... args) {
        return findConstructor(clazz, objectsToClasses(args));
    }

    public static <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            Constructor<T> constructor = discoverConstructor(clazz, parameterTypes);
            if (constructor == null) throw new RuntimeException(e);
            return constructor;
        }
    }

    private static <T> Constructor<T> discoverConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        for (Constructor<?> c : clazz.getConstructors())
            if (areCompatible(c.getParameterTypes(), parameterTypes)) return (Constructor<T>)c;
        return null;
    }

    public static Method findMethod(Class<?> clazz, String methodName, Object... args) {
        return findMethod(clazz, methodName, objectsToClasses(args));
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName,  parameterTypes);
        } catch (NoSuchMethodException e) {
            Method method = discoverMethod(clazz, methodName, parameterTypes);
            if (method == null) throw new RuntimeException(e);
            return method;
        }
    }

    private static Method discoverMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        for (Method m : clazz.getMethods())
            if (m.getName().equals(methodName) && areCompatible(m.getParameterTypes(), parameterTypes)) return m;
        return null;
    }

    private static boolean areCompatible(Class<?>[] methodParams, Class<?>[] actualParam) {
        if (methodParams == null || methodParams.length != actualParam.length) return false;
        for (int i = 0; i < methodParams.length; i++)
            if (!areCompatible(methodParams[i], actualParam[i])) return false;
        return true;
    }

    private static boolean areCompatible(Class<?> methodParam, Class<?> actualParam) {
        return methodParam.isAssignableFrom(actualParam) ||
                (methodParam.isPrimitive() ? areBoxingCompatible(methodParam, actualParam) : areBoxingCompatible(actualParam, methodParam));
    }

    private static boolean areBoxingCompatible(Class<?> primitiveClass, Class<?> boxedClass) {
         return boxedClass.getSimpleName().toLowerCase().startsWith(primitiveClass.getSimpleName());
    }

    private static Class<?>[] objectsToClasses(Object... args) {
        Class<?>[] parameterTypes = new Class<?>[args == null ? 0 : args.length];
        for (int i = 0; i < parameterTypes.length; i++) parameterTypes[i] = args[i].getClass();
        return parameterTypes;
    }
}
