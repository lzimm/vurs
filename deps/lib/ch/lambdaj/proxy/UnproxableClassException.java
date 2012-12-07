// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.proxy;

/**
 * This invocation is thrown while trying to proxy an object of an unproxeable (final) class.
 * @author Mario Fusco
 */
public class UnproxableClassException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	UnproxableClassException(Class<?> clazz, Exception cause) {
		super("Unable to proxy the final class " + clazz.getName(),	cause);
	}
}
