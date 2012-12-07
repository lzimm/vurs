// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.matcher;

import org.hamcrest.*;

/**
 * A matcher that logically combines a set of matchers returning true if all of them satisfy its own condition.
 * @author Mario Fusco
 */
public class AndMatcher<T> extends LambdaJMatcher<T> {
	
	private final Matcher<T>[] matchers;

	public AndMatcher(Matcher<T>... matchers) {
		this.matchers = matchers;
	}
	
	public boolean matches(Object item) {
		for (Matcher<T> matcher : matchers) if (!matcher.matches(item)) return false;
		return true;
	}

    @Factory
    public static <T> AndMatcher<T> and(Matcher<T>... matchers) {
    	return new AndMatcher<T>(matchers);
    }
}
