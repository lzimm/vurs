// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.group;

import static ch.lambdaj.function.argument.ArgumentsFactory.*;
import ch.lambdaj.function.argument.*;

/**
 * A GroupCondition that allows to group items based on the value they have on a given argument
 * @author Mario Fusco
 */
public class ArgumentGroupCondition<T> extends GroupCondition<Argument<?>> {
	
	private final Argument<T> groupBy;
	private final String groupName;

	ArgumentGroupCondition(T argument) {
		groupBy = actualArgument(argument);
		groupName = groupBy.getInkvokedPropertyName();
	}
	
	protected String getGroupName() {
		return groupName;
	}

    protected Object getGroupValue(Object item) {
        return groupBy.evaluate(item);
    }

    /**
     * Sets an alias for the groups created using this condition
     * @param alias The alias to be set
     * @return The GroupCondition itself in order to allow a fluent interface
     */
	@Override
	public ArgumentGroupCondition as(String alias) {
		return (ArgumentGroupCondition)super.as(alias);
	}

	public ArgumentGroupCondition head(Object argument) {
		Argument<?> actualArgument = actualArgument(argument);
		additionalProperties.put(actualArgument.getInkvokedPropertyName(), actualArgument);
		return this;
	}

	public ArgumentGroupCondition head(Object argument, String alias) {
		additionalProperties.put(alias, actualArgument(argument));
		return this;
	}

	protected String getAdditionalPropertyValue(String name, Object item) {
		return asNotNullString(additionalProperties.get(name).evaluate(item));
	}
}
