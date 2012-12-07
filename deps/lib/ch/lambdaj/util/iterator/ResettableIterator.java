// Modified or written by Ex Machina SAGL for inclusion with lambdaj.
// Copyright (c) 2009 Mario Fusco.
// Licensed under the Apache License, Version 2.0 (the "License")

package ch.lambdaj.util.iterator;

import java.util.*;

/**
 * @author Mario Fusco
 */
public abstract class ResettableIterator<T> implements Iterator<T> {

    public abstract void reset();

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
