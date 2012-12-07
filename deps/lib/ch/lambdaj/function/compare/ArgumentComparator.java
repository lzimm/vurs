// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.compare;

import static ch.lambdaj.function.argument.ArgumentsFactory.*;

import java.util.*;

import ch.lambdaj.function.argument.*;

/**
 * Compares two objects by comparing the values returned by an Argument call on them.
 * @author Mario Fusco
 */
public class ArgumentComparator<T, A> implements Comparator<T> {

	private final Argument<A> argument;
	private final Comparator<A> comparator;
	
	public ArgumentComparator(A argument) {
		this(actualArgument(argument));
	}

	public ArgumentComparator(Argument<A> argument) {
		this(argument, null);
	}
	
	public ArgumentComparator(A argument, Comparator<A> comparator) {
		this(actualArgument(argument), comparator);
	}
	
    @SuppressWarnings("unchecked")
	public ArgumentComparator(Argument<A> argument, Comparator<A> comparator) {
		this.argument = argument;
		this.comparator = comparator != null ? comparator : (Comparator<A>)DEFAULT_ARGUMENT_COMPARATOR;
	}
	
	public int compare(T o1, T o2) {
		A val1 = argument.evaluate(o1);
		A val2 = argument.evaluate(o2);
		if (val1 == null && val2 == null) return 0;
		return comparator.compare(val1, val2);
	}
	
	private static final Comparator<?> DEFAULT_ARGUMENT_COMPARATOR = new DefaultArgumentComparator();

	private static class DefaultArgumentComparator implements Comparator<Object> {
		@SuppressWarnings("unchecked")
		public int compare(Object val1, Object val2) {
			return val1 != null ? ((Comparable)val1).compareTo(val2) : -((Comparable)val2).compareTo(null);
		}
	}
}