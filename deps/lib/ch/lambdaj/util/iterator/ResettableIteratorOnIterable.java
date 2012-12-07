// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.util.iterator;

import java.util.*;

/**
 * @author Mario Fusco
 */
public class ResettableIteratorOnIterable<T> extends ResettableIterator<T> {

    private final Iterable<T> iterable;

    private Iterator<T> iterator;

    public ResettableIteratorOnIterable(Iterable<T> iterable) {
        this.iterable = iterable;
        reset();
    }

    public void reset() {
        iterator = iterable.iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        return iterator.next();
    }
}
