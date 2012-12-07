// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.aggregate;

/**
 * A PairAggregator that is initialized to the value of its idempotent item.
 * @author Mario Fusco
 */
public abstract class InitializedPairAggregator<T> extends PairAggregator<T> {

    private final T firstItem;

    public InitializedPairAggregator(T firstItem) {
        this.firstItem = firstItem;
    }

    public final T emptyItem() {
        return firstItem;
    }
}
