// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.aggregate;

/**
 * An aggregator that sums ints
 * @author Mario Fusco
 */
public class SumInteger extends InitializedPairAggregator<Integer> {

    public SumInteger(Integer firstItem) {
        super(firstItem);
    }

    /**
     * Aggregates two ints by summing them
     * @param first The first int to be summed
     * @param second The second int to be summed
     * @return The sum of the two ints
     */
	public Integer aggregate(Integer first, Integer second) {
		return first + second;
	}
}