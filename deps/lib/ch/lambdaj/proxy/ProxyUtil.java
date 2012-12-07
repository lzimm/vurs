// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.proxy;

import java.lang.reflect.*;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * An utility class of static factory methods that provide facilities to create proxies
 * @author Mario Fusco
 * @author Sebastian Jancke
 */
@SuppressWarnings("unchecked")
public final class ProxyUtil {
	
	private ProxyUtil() { }
	
    // ////////////////////////////////////////////////////////////////////////
    // /// Generic Proxy
    // ////////////////////////////////////////////////////////////////////////

    public static boolean isProxable(Class<?> clazz) {
        return !clazz.isPrimitive() && !Modifier.isFinal(clazz.getModifiers()) && !clazz.isAnonymousClass();
    }
	
	public static <T> T createProxy(InvocationInterceptor interceptor, Class<T> clazz, boolean failSafe, Class<?> ... implementedInterface) {
		if (clazz.isInterface()) return (T)createNativeJavaProxy(clazz.getClassLoader(), interceptor, concatClasses(new Class<?>[] { clazz }, implementedInterface));

		try {
			return (T)createEnhancer(interceptor, clazz, implementedInterface).create();
		} catch (IllegalArgumentException iae) {
			if (Proxy.isProxyClass(clazz)) return (T)createNativeJavaProxy(clazz.getClassLoader(), interceptor, concatClasses(implementedInterface, clazz.getInterfaces()));
			if (isProxable(clazz)) return ClassImposterizer.INSTANCE.imposterise(interceptor, clazz, implementedInterface);
			if (failSafe) return null;
			throw new UnproxableClassException(clazz, iae);
		}
	}

    // ////////////////////////////////////////////////////////////////////////
    // /// Void Proxy
    // ////////////////////////////////////////////////////////////////////////
    
	public static <T> T createVoidProxy(Class<T> clazz) {
		return createProxy(InvocationInterceptor.VOID, clazz, false, InvocationInterceptor.VoidInterceptor.class);
	}
	
    // ////////////////////////////////////////////////////////////////////////
    // /// Iterable Proxy
    // ////////////////////////////////////////////////////////////////////////
    
	public static <T> T createIterableProxy(InvocationInterceptor interceptor, Class<T> clazz) {
        if (clazz.isPrimitive()) return null;
        if (clazz == String.class) clazz = (Class<T>)CharSequence.class;
        return createProxy(interceptor, clazz, false, Iterable.class);
	}

    // ////////////////////////////////////////////////////////////////////////
    // /// Private
    // ////////////////////////////////////////////////////////////////////////
    
    private static Enhancer createEnhancer(MethodInterceptor interceptor, Class<?> clazz, Class<?> ... interfaces) {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(interceptor);
        enhancer.setSuperclass(clazz);
        if (interfaces != null && interfaces.length > 0) enhancer.setInterfaces(interfaces);
        return enhancer;
    }
	
    private static Object createNativeJavaProxy(ClassLoader classLoader, InvocationHandler interceptor, Class<?> ... interfaces) {
        return Proxy.newProxyInstance(classLoader, interfaces, interceptor);
    }
	
	private static Class<?>[] concatClasses(Class<?>[] first, Class<?>[] second) {
        if (first == null || first.length == 0) return second;
        if (second == null || second.length == 0) return first;
        Class<?>[] concatClasses = new Class[first.length + second.length];
        System.arraycopy(first, 0, concatClasses, 0, first.length);
        System.arraycopy(second, 0, concatClasses, first.length, second.length);
        return concatClasses;
	}
}
