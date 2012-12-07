// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.proxy;

import static ch.lambdaj.Lambda.*;

import java.lang.reflect.*;

import ch.lambdaj.function.aggregate.*;
import ch.lambdaj.util.iterator.*;

/**
 * Proxies a list of objects in order to seamlessly aggregate their value by exposing the API of a single object.
 * @author Mario Fusco
 */
public class ProxyAggregator<T, A> extends ProxyIterator<T> {

	private final Aggregator<A> aggregator;

	protected ProxyAggregator(ResettableIterator<T> proxiedIterator, Aggregator<A> aggregator) {
		super(proxiedIterator);
		this.aggregator = aggregator;
	}

	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		return aggregate(iterateOnValues(method, args), aggregator);
	}

	@SuppressWarnings("unchecked")
	public static <T, A> T createProxyAggregator(ResettableIterator<T> proxiedIterator, Aggregator<A> aggregator, Class<?> clazz) {
		return (T)ProxyUtil.createIterableProxy(new ProxyAggregator<T, A>(proxiedIterator, aggregator), clazz);
	}
}
