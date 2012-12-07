// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.util.iterator;

import java.util.*;

/**
 * @author Mario Fusco
 */
public class ResettableIteratorOnIterator<T> extends ResettableIterator<T> {

    private final Iterator<T> iterator;

    private final List<T> innerIterable = new ArrayList<T>();
    private Iterator<T> innerIterator;

    private final List<T> cache = new ArrayList<T>();

    public ResettableIteratorOnIterator(Iterator<T> iterator) {
        this.iterator = iterator;
        innerIterator = innerIterable.iterator();
    }

    public void reset() {
        innerIterable.addAll(cache);
        cache.clear();
        innerIterator = innerIterable.iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext() || innerIterator.hasNext();
    }

    public T next() {
        if (innerIterator.hasNext()) return innerIterator.next();
        if (iterator.hasNext()) {
            T next = iterator.next();
            cache.add(next);
            return next;
        }
        throw new NoSuchElementException();
    }
}