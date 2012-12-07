// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.matcher;

import org.hamcrest.*;


/**
 * A Matcher that extends the Hamcrest BaseMatcher by adding two fluent interface style methods that
 * allow to logically combine two matchers.
 * @author Mario Fusco
 */
public abstract class LambdaJMatcher<T> extends BaseMatcher<T> {

	@SuppressWarnings("unchecked")
	public LambdaJMatcher<T> and(Matcher<T> matcher) {
		return AndMatcher.and(this, matcher);
	}

	@SuppressWarnings("unchecked")
	public LambdaJMatcher<T> or(Matcher<T> matcher) {
		return OrMatcher.or(this, matcher);
	}

    public void describeTo(Description description) { }
}
