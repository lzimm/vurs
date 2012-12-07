// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.proxy;

import static ch.lambdaj.proxy.ProxyUtil.*;
import ch.lambdaj.util.iterator.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Proxies a list of objects in order to seamlessly iterate on them by exposing the API of a single object.
 * @author Mario Fusco
 */
public class ProxyIterator<T> extends InvocationInterceptor implements Iterable<T> {

	private final ResettableIterator<? extends T> proxiedIterator;

	protected ProxyIterator(ResettableIterator<? extends T> proxiedIterator) {
        this.proxiedIterator = proxiedIterator;
	}

	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("iterator")) return iterator();
		return createProxyIterator(iterateOnValues(method, args), (Class<Object>)method.getReturnType());
	}

	protected ResettableIterator<Object> iterateOnValues(Method method, Object[] args) throws Throwable {
        proxiedIterator.reset();
        List<Object> list = new ArrayList<Object>();
        while (proxiedIterator.hasNext()) list.add(method.invoke(proxiedIterator.next(), args));
		return new ResettableIteratorOnIterable(list);
	}

	public static <T> T createProxyIterator(ResettableIterator<? extends T> proxiedIterator, Class<T> clazz) {
		return createIterableProxy(new ProxyIterator<T>(proxiedIterator), clazz);
	}

    public static <T> T createProxyIterator(ResettableIterator<? extends T> proxiedIterator, T firstItem) {
        T proxy = createProxyIterator(proxiedIterator, (Class<T>)firstItem.getClass());
        proxiedIterator.reset();
        return proxy;
    }

    @SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>)proxiedIterator;
	}
}
