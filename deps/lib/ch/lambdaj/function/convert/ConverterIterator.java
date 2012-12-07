// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.function.convert;

import java.util.*;

/**
 * An Iterator that converts its items using the given Converter while iterating on them.
 * @author Mario Fusco
 */
public class ConverterIterator<F, T> implements Iterator<T> {

    private final Converter<F, T> converter;
    private final Iterator<F> iterator;

    public ConverterIterator(Converter<F, T> converter, Iterator<F> iterator) {
        this.converter = converter;
        this.iterator = iterator;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        return converter.convert(iterator.next());
    }

    public void remove() {
        iterator.remove();
    }
}
