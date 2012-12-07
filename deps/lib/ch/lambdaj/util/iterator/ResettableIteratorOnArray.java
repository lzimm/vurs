// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.util.iterator;

import java.util.*;

/**
 * @author Mario Fusco
 */
public class ResettableIteratorOnArray<T> extends ResettableIterator<T> {

    private final T[] array;
    private int counter = 0;

    public ResettableIteratorOnArray(T[] array) {
        this.array = array;
    }

    public boolean hasNext() {
        return counter < array.length;
    }

    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return array[counter++];
    }

    public void reset() {
        counter = 0;
    }
}