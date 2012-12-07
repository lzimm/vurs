// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.group;

import static ch.lambdaj.function.argument.ArgumentsFactory.*;

import ch.lambdaj.function.argument.*;

import java.util.*;

/**
 * A comparator that allows to sort group based on the value assumed on the given argument
 * by the object that is the key for a group itself
 * @author Mario Fusco
 */
class GroupComparator<A extends Comparable<A>> implements Comparator<GroupItem<?>> {

    private final Argument<A> argument;

    public GroupComparator(A argument) {
        this(actualArgument(argument));
    }

    public GroupComparator(Argument<A> argument) {
        this.argument = argument;
    }

    public int compare(GroupItem<?> group1, GroupItem<?> group2) {
        A val1 = argument.evaluate(group1.getGroupKey());
        A val2 = argument.evaluate(group2.getGroupKey());
        if (val1 == null && val2 == null) return 0;
        return val1 != null ? val1.compareTo(val2) : -val2.compareTo(null);
    }
}
