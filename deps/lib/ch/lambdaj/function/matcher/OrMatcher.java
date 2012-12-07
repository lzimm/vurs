// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.matcher;

import org.hamcrest.*;

/**
 * A matcher that logically combines a set of matchers returning true if at least one of them satisfies its own condition.
 * @author Mario Fusco
 */
public class OrMatcher<T> extends LambdaJMatcher<T> {
	
	private final Matcher<T>[] matchers;

	public OrMatcher(Matcher<T>... matchers) {
		this.matchers = matchers;
	}
	
	public boolean matches(Object item) {
		for (Matcher<T> matcher : matchers) if (matcher.matches(item)) return true;
		return false;
	}

    @Factory
    public static <T> OrMatcher<T> or(Matcher<T>... matchers) {
    	return new OrMatcher<T>(matchers);
    }
}